import java.util.Objects;

public class Pet {
    private final String species;
    private final String name;
    private int age;
    private String healthStatus;
    private String adopterName;

    public Pet(String species, String name, int age, String healthStatus) {
        this.species = species;
        this.name = name;
        this.age = age;
        this.healthStatus = healthStatus;
        this.adopterName = null;
    }

    public static Pet fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) return null;

        String species = parts[0];
        String name = parts[1];
        int age = Integer.parseInt(parts[2]);
        String healthStatus = parts[3];
        Pet pet = new Pet(species, name, age, healthStatus);
        if (parts.length > 4 && !parts[4].equals("null")) {
            pet.setAdopterName(parts[4]);
        }
        return pet;
    }

    public String getSpecies() {
        return species;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getAdopterName() {
        return adopterName;
    }

    public void setAdopterName(String adopterName) {
        this.adopterName = adopterName;
    }

    @Override
    public String toString() {
        String adoptedBy = (adopterName != null && !adopterName.isEmpty()) ? ", Adopted by: " + adopterName : ", Available";
        return String.format("Species: %s, Name: %s, Age: %d, Health: %s%s", species, name, age, healthStatus, adoptedBy);
    }

    public String toFileString() {
        return String.join(",", species, name, String.valueOf(age), healthStatus, adopterName == null ? "null" : adopterName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(name.toLowerCase(), pet.name.toLowerCase()) && Objects.equals(species.toLowerCase(), pet.species.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), species.toLowerCase());
    }
}