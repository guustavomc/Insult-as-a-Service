from unittest.mock import patch

from fastapi.testclient import TestClient

from main import app

client = TestClient(app)

def test_healt_return_OK():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "ok"

def test_insult_returns_200_on_valid_request():
    response = client.post("/insult", json={
        "name": "John",
        "characteristics": ["slow", "arrogant"]
    })
    assert response.status_code == 200

def test_insult_response_contains_name():
    response = client.post("/insult", json={
        "name": "John",
        "characteristics": ["slow", "arrogant"]
    })
    assert response.status_code == 200
    assert "John" in response.json()["insult"]

def test_insult_returns_422_when_name_is_blank():
    response = client.post("/insult", json={
        "name": "",
        "characteristics": ["slow", "arrogant"]
    })
    assert response.status_code == 422

def test_insult_returns_422_when_name_is_missing():
    response = client.post("/insult", json={
        "characteristics": ["slow", "arrogant"]
    })
    assert response.status_code == 422

def test_insult_returns_422_when_characteristics_is_missing():
    response = client.post("/insult", json={
            "name": "John"
        })
    assert response.status_code == 422

def test_insult_delegates_to_generator():
    with patch("main.generate_insult", return_value="mocked insult") as mock_gen:
        response = client.post("/insult", json={
            "name": "John",
            "characteristics": ["slow"]
        })