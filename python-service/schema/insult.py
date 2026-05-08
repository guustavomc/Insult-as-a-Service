from pydantic import BaseModel, ConfigDict

class InsultRequest(BaseModel):
    name: str
    characteristics: list[str]

    @field_validator("name")
    @classmethod
    def name_must_not_be_blank(cls, v: str) -> str:
        if not v.strip():
            raise ValueError("name must not be blank")
        return v


class InsultResponse(BaseModel):
    insult: str