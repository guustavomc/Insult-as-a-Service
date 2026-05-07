import random

_NOUNS = [
    "walnut", "turnip", "cabbage", "doorknob", "noodle",
    "disaster", "potato", "gremlin", "monument", "relic",
]

_TEMPLATES = [
    "{name}, you magnificently {trait} {noun}.",
    "{name}, you are a {trait} {noun} of the highest order.",
    "Behold {name}: the world's most {trait} {noun}.",
    "{name}, you {trait} excuse for a {noun}.",
    "The audacity of {name}, a truly {trait} {noun}.",
]

def generate_insult(name: str, characteristics: list[str]) -> str:
    trait = random.choice(characteristics)  if characteristics else "unremarkable"
    noun = random.choice(_NOUNS)
    template = random.choice(_TEMPLATES)
    return template.format(name=name, trait=trait, noun=noun)