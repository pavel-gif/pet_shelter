import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Adopter {
    private final String name;
    private String phone;
    private final List<String> adoptedPetNames;

    public Adopter(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.adoptedPetNames = new ArrayList<>();
    }

    public static Adopter fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 2) return null;

        String name = parts[0];
        String phone = parts[1];
        Adopter adopter = new Adopter(name, phone);

        if (parts.length > 2 && !parts[2].equals("none") && !parts[2].isEmpty()) {
            String[] petIdentifiers = parts[2].split(";");
            for (String petId : petIdentifiers) {
                if (!petId.trim().isEmpty()) {
                    adopter.adoptedPetNames.add(petId);
                }
            }
        }
        return adopter;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getAdoptedPetNames() {
        return adoptedPetNames;
    }

    public void addAdoptedPet(Pet pet) {
        String petIdentifier = pet.getSpecies() + ":" + pet.getName();
        if (!adoptedPetNames.contains(petIdentifier)) {
            adoptedPetNames.add(petIdentifier);
        }
    }

    public void removeAdoptedPet(Pet pet) {
        String petIdentifier = pet.getSpecies() + ":" + pet.getName();
        adoptedPetNames.remove(petIdentifier);
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Phone: %s, Adopted Pets: %d", name, phone, adoptedPetNames.size());
    }

    public String toFileString() {
        String petsString = String.join(";", adoptedPetNames);
        if (petsString.isEmpty()) petsString = "none";
        return String.join(",", name, phone, petsString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adopter adopter = (Adopter) o;
        return Objects.equals(name.toLowerCase(), adopter.name.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
}