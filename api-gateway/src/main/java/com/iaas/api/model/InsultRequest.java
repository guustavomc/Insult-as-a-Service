package main.java.com.iaas.api.model;

import java.util.List;

public class InsultRequest {

    @NotBlank
    private String name;

    @NotNull
    private List<String> characteristics;


    public String getName() {
        return name;
    }

    public List<String> getCharacteristics() {
        return characteristics;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCharacteristics(List<String> characteristics) {
        this.characteristics = characteristics;
    }
}
