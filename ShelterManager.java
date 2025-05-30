import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShelterManager {
    private static final String PETS_FILE = "pets.txt";
    private static final String ADOPTERS_FILE = "adopters.txt";
    private final List<Pet> pets;
    private final List<Adopter> adopters;

    public ShelterManager() {
        pets = new ArrayList<>();
        adopters = new ArrayList<>();
        loadData();
    }

    public boolean addPet(Pet pet) {
        if (pets.contains(pet)) {
            return false;
        }
        pets.add(pet);
        return true;
    }

    public boolean removePet(Pet pet) {
        if (pet.getAdopterName() != null) {
            Adopter adopter = findAdopterByName(pet.getAdopterName());
            if (adopter != null) {
                adopter.removeAdoptedPet(pet);
            }
        }
        return pets.remove(pet);
    }

    public Pet findPetByNameAndSpecies(String name, String species) {
        for (Pet pet : pets) {
            if (pet.getName().equalsIgnoreCase(name) && pet.getSpecies().equalsIgnoreCase(species)) {
                return pet;
            }
        }
        return null;
    }

    public List<Pet> getAllPets() {
        return new ArrayList<>(pets);
    }

    public void updatePetHealth(Pet pet, String newHealthStatus) {
        pet.setHealthStatus(newHealthStatus);
    }

    public boolean addAdopter(Adopter adopter) {
        if (adopters.contains(adopter)) {
            return false;
        }
        adopters.add(adopter);
        return true;
    }

    public boolean removeAdopter(Adopter adopter) {
        for (String petIdentifier : adopter.getAdoptedPetNames()) {
            String[] parts = petIdentifier.split(":");
            if (parts.length == 2) {
                Pet pet = findPetByNameAndSpecies(parts[1], parts[0]);
                if (pet != null) {
                    pet.setAdopterName(null);
                }
            }
        }
        return adopters.remove(adopter);
    }

    public Adopter findAdopterByName(String name) {
        for (Adopter adopter : adopters) {
            if (adopter.getName().equalsIgnoreCase(name)) {
                return adopter;
            }
        }
        return null;
    }

    public List<Adopter> getAllAdopters() {
        return new ArrayList<>(adopters);
    }

    public boolean adoptPet(Pet pet, Adopter adopter) {
        if (pet.getAdopterName() != null && !pet.getAdopterName().isEmpty()) {
            return false;
        }
        pet.setAdopterName(adopter.getName());
        adopter.addAdoptedPet(pet);
        return true;
    }

    public boolean returnPetToShelter(Pet pet) {
        if (pet.getAdopterName() == null) {
            return false;
        }
        Adopter adopter = findAdopterByName(pet.getAdopterName());
        if (adopter != null) {
            adopter.removeAdoptedPet(pet);
        }
        pet.setAdopterName(null);
        return true;
    }

    public List<Pet> sortPetsByAge() {
        List<Pet> sortedPets = new ArrayList<>(pets);
        for (int i = 1; i < sortedPets.size(); i++) {
            Pet current = sortedPets.get(i);
            int j = i - 1;
            while (j >= 0 && sortedPets.get(j).getAge() > current.getAge()) {
                sortedPets.set(j + 1, sortedPets.get(j));
                j--;
            }
            sortedPets.set(j + 1, current);
        }
        return sortedPets;
    }

    public List<Pet> filterPetsBySpecies(String species) {
        if (species == null || species.trim().isEmpty() || species.equalsIgnoreCase("All")) {
            return getAllPets();
        }
        return pets.stream().filter(p -> p.getSpecies().equalsIgnoreCase(species)).collect(Collectors.toList());
    }

    public List<Pet> filterPetsByAge(int minAge, int maxAge) {
        return pets.stream().filter(p -> p.getAge() >= minAge && p.getAge() <= maxAge).collect(Collectors.toList());
    }

    public List<String> getUniqueSpecies() {
        return pets.stream().map(Pet::getSpecies).distinct().sorted().collect(Collectors.toList());
    }

    public void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PETS_FILE))) {
            for (Pet pet : pets) {
                writer.println(pet.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving pets: " + e.getMessage());
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(ADOPTERS_FILE))) {
            for (Adopter adopter : adopters) {
                writer.println(adopter.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving adopters: " + e.getMessage());
        }
    }

    public void loadData() {
        File petsFile = new File(PETS_FILE);
        if (petsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(petsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Pet pet = Pet.fromFileString(line);
                    if (pet != null) {
                        pets.add(pet);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading pets: " + e.getMessage());
            }
        }

        File adoptersFile = new File(ADOPTERS_FILE);
        if (adoptersFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(adoptersFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Adopter adopter = Adopter.fromFileString(line);
                    if (adopter != null) {
                        adopters.add(adopter);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading adopters: " + e.getMessage());
            }
        }
    }
}