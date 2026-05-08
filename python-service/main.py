from fastapi import FastAPI

from insult_generator import generate_insult
from schema.insult import InsultRequest, InsultResponse

app = FastAPI()


@app.post("/insult", response_model=InsultResponse)
def insult(request: InsultRequest) -> InsultRequest:
    result = generate_insult(request.name, request.characteristics)
    return InsultResponse(insult=result)