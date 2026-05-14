from unittest.mock import patch

from insult_generator import generate_insult

def test_generate_insult_return_string():
    result = generate_insult("John", ["slow", "arrogant"])
    assert isinstance(result, str)


def test_generate_insult_assert_name_in_result():
    result = generate_insult("John", ["slow", "arrogant"])
    assert "John" in result

def test_generate_insult_return_unremarkable_when_characteristics_is_empty():
    result = generate_insult("John", [])
    assert "unremarkable" in result

def test_generate_insult_assert_characteristics_in_result():
    result = generate_insult("John", ["slow"])
    assert "slow" in result

def test_deterministic_output_with_mocked_random():
    fixed_template = "Behold {name}: the world's most {trait} {noun}."
    with patch("insult_generator.random.choice", side_effect=["arrogant", "walnut", fixed_template]):
        result = generate_insult("John", ["arrogant"])
    assert result == "Behold John: the world's most arrogant walnut."