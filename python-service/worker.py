import os
import redis
from insult_generator import generate_insult

STREAM_KEY = "insult-requests"
GROUP_NAME = "python-workers"
CONSUMER_NAME = "worker-1"
RESULT_TTL = 300

r = redis.Redis(
    host=os.getenv("REDIS_HOST", "localhost"),
    port=int(os.getenv("REDIS_PORT", 6379)),
    decode_responses=True
)


def create_consumer_group():
    try:
        r.xgroup_create(STREAM_KEY, GROUP_NAME, id="0", mkstream=True)
        print(f"Consumer group '{GROUP_NAME}' created.")
    except redis.exceptions.ResponseError as e:
        if "BUSYGROUP" not in str(e):
            raise


def process_message(message_id, fields):
    job_id = fields["jobId"]
    name = fields["name"]
    characteristics = fields["characteristics"].split(",")

    insult = generate_insult(name, characteristics)

    r.setex(f"result:{job_id}", RESULT_TTL, insult)
    r.xack(STREAM_KEY, GROUP_NAME, message_id)
    print(f"Job {job_id} done: {insult}")


def run():
    create_consumer_group()
    print("Worker listening for messages...")

    while True:
        messages = r.xreadgroup(
            GROUP_NAME, CONSUMER_NAME,
            {STREAM_KEY: ">"},
            count=10,
            block=2000
        )
        if messages:
            for stream, entries in messages:
                for message_id, fields in entries:
                    process_message(message_id, fields)


if __name__ == "__main__":
    run()
