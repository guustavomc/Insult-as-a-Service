from fastapi import FastAPI

from insult_generator import generate_insult
from schema.insult import InsultRequest, InsultResponse
from prometheus_fastapi_instrumentator import Instrumentator

app = FastAPI()
Instrumentator().instrument(app).expose(app)


@app.post("/insult", response_model=InsultResponse)
def insult(request: InsultRequest) -> InsultRequest:
    result = generate_insult(request.name, request.characteristics)
    return InsultResponse(insult=result)

@app.get("/health")
def health() -> dict:
    return {"status": "ok"}