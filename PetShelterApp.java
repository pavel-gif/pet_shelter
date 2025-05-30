import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.stream.Collectors;

public class PetShelterApp extends JFrame {
    private final ShelterManager manager;

    private JList<Pet> petList;
    private DefaultListModel<Pet> petListModel;
    private JButton addPetButton, editPetButton, deletePetButton, sortPetsButton, adoptPetButton, returnPetButton;
    private JComboBox<String> speciesFilterComboBox;
    private JTextField ageFilterMinField, ageFilterMaxField, searchPetField;
    private JButton filterPetsButton, searchPetButton, clearPetFilterButton;

    private JList<Adopter> adopterList;
    private DefaultListModel<Adopter> adopterListModel;
    private JButton addAdopterButton, editAdopterButton, deleteAdopterButton, searchAdopterButton;
    private JTextField searchAdopterField;


    public PetShelterApp() {
        manager = new ShelterManager();

        setTitle("Pet Shelter Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                manager.saveData();
                System.out.println("Data saved. Exiting.");
                System.exit(0);
            }
        });

        initComponents();
        layoutComponents();
        attachListeners();

        refreshPetList(manager.getAllPets());
        refreshAdopterList(manager.getAllAdopters());
        updateSpeciesFilter();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PetShelterApp app = new PetShelterApp();
            app.setVisible(true);
        });
    }

    private void initComponents() {
        petListModel = new DefaultListModel<>();
        petList = new JList<>(petListModel);
        petList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addPetButton = new JButton("Add Pet");
        editPetButton = new JButton("Edit Pet");
        editPetButton.setEnabled(false);
        deletePetButton = new JButton("Delete Pet");
        deletePetButton.setEnabled(false);
        sortPetsButton = new JButton("Sort by Age (Asc)");
        adoptPetButton = new JButton("Adopt Selected Pet");
        adoptPetButton.setEnabled(false);
        returnPetButton = new JButton("Return Selected Pet");
        returnPetButton.setEnabled(false);

        speciesFilterComboBox = new JComboBox<>();
        ageFilterMinField = new JTextField(3);
        ageFilterMaxField = new JTextField(3);
        filterPetsButton = new JButton("Filter Pets");
        searchPetField = new JTextField(15);
        searchPetButton = new JButton("Search Pet by Name");
        clearPetFilterButton = new JButton("Clear Filters/Search");

        adopterListModel = new DefaultListModel<>();
        adopterList = new JList<>(adopterListModel);
        adopterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addAdopterButton = new JButton("Add Adopter");
        editAdopterButton = new JButton("Edit Adopter");
        editAdopterButton.setEnabled(false);
        deleteAdopterButton = new JButton("Delete Adopter");
        deleteAdopterButton.setEnabled(false);
        searchAdopterField = new JTextField(15);
        searchAdopterButton = new JButton("Search Adopter by Name");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel petPanel = new JPanel(new BorderLayout(5, 5));
        petPanel.setBorder(BorderFactory.createTitledBorder("Pets Management"));

        JPanel petFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        petFilterPanel.add(new JLabel("Species:"));
        petFilterPanel.add(speciesFilterComboBox);
        petFilterPanel.add(new JLabel("Age (Min-Max):"));
        petFilterPanel.add(ageFilterMinField);
        petFilterPanel.add(new JLabel("-"));
        petFilterPanel.add(ageFilterMaxField);
        petFilterPanel.add(filterPetsButton);
        petFilterPanel.add(new JLabel("Name:"));
        petFilterPanel.add(searchPetField);
        petFilterPanel.add(searchPetButton);
        petFilterPanel.add(clearPetFilterButton);

        petPanel.add(petFilterPanel, BorderLayout.NORTH);
        petPanel.add(new JScrollPane(petList), BorderLayout.CENTER);

        JPanel petActionPanel = new JPanel(new FlowLayout());
        petActionPanel.add(addPetButton);
        petActionPanel.add(editPetButton);
        petActionPanel.add(deletePetButton);
        petActionPanel.add(sortPetsButton);
        petActionPanel.add(adoptPetButton);
        petActionPanel.add(returnPetButton);
        petPanel.add(petActionPanel, BorderLayout.SOUTH);

        JPanel adopterPanel = new JPanel(new BorderLayout(5, 5));
        adopterPanel.setBorder(BorderFactory.createTitledBorder("Adopters Management"));

        JPanel adopterSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adopterSearchPanel.add(new JLabel("Name:"));
        adopterSearchPanel.add(searchAdopterField);
        adopterSearchPanel.add(searchAdopterButton);
        adopterPanel.add(adopterSearchPanel, BorderLayout.NORTH);

        adopterPanel.add(new JScrollPane(adopterList), BorderLayout.CENTER);

        JPanel adopterActionPanel = new JPanel(new FlowLayout());
        adopterActionPanel.add(addAdopterButton);
        adopterActionPanel.add(editAdopterButton);
        adopterActionPanel.add(deleteAdopterButton);
        adopterPanel.add(adopterActionPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, petPanel, adopterPanel);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);
    }

    private void attachListeners() {
        petList.addListSelectionListener(e -> {
            boolean selected = !petList.isSelectionEmpty();
            editPetButton.setEnabled(selected);
            deletePetButton.setEnabled(selected);
            adoptPetButton.setEnabled(selected && petList.getSelectedValue().getAdopterName() == null);
            returnPetButton.setEnabled(selected && petList.getSelectedValue().getAdopterName() != null);
        });

        adopterList.addListSelectionListener(e -> {
            boolean selected = !adopterList.isSelectionEmpty();
            editAdopterButton.setEnabled(selected);
            deleteAdopterButton.setEnabled(selected);
        });

        addPetButton.addActionListener(e -> showAddPetDialog());
        editPetButton.addActionListener(e -> showEditPetDialog(petList.getSelectedValue()));
        deletePetButton.addActionListener(e -> deleteSelectedPet());
        sortPetsButton.addActionListener(e -> {
            refreshPetList(manager.sortPetsByAge());
            JOptionPane.showMessageDialog(this, "Pets sorted by age.", "Sort", JOptionPane.INFORMATION_MESSAGE);
        });
        adoptPetButton.addActionListener(e -> showAdoptPetDialog(petList.getSelectedValue()));
        returnPetButton.addActionListener(e -> returnSelectedPet(petList.getSelectedValue()));

        filterPetsButton.addActionListener(e -> filterAndRefreshPets());
        searchPetButton.addActionListener(e -> searchAndRefreshPets());
        clearPetFilterButton.addActionListener(e -> {
            speciesFilterComboBox.setSelectedIndex(0);
            ageFilterMinField.setText("");
            ageFilterMaxField.setText("");
            searchPetField.setText("");
            refreshPetList(manager.getAllPets());
        });

        addAdopterButton.addActionListener(e -> showAddAdopterDialog());
        editAdopterButton.addActionListener(e -> showEditAdopterDialog(adopterList.getSelectedValue()));
        deleteAdopterButton.addActionListener(e -> deleteSelectedAdopter());
        searchAdopterButton.addActionListener(e -> searchAndRefreshAdopters());
    }

    private void refreshPetList(List<Pet> petsToShow) {
        petListModel.clear();
        for (Pet pet : petsToShow) {
            petListModel.addElement(pet);
        }
    }

    private void refreshAdopterList(List<Adopter> adoptersToShow) {
        adopterListModel.clear();
        for (Adopter adopter : adoptersToShow) {
            adopterListModel.addElement(adopter);
        }
    }

    private void updateSpeciesFilter() {
        speciesFilterComboBox.removeAllItems();
        speciesFilterComboBox.addItem("All");
        List<String> species = manager.getUniqueSpecies();
        for (String s : species) {
            speciesFilterComboBox.addItem(s);
        }
    }

    private void showAddPetDialog() {
        JTextField nameField = new JTextField(15);
        JTextField speciesField = new JTextField(15);
        JTextField ageField = new JTextField(5);
        JTextField healthField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Species:"));
        panel.add(speciesField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Health Status:"));
        panel.add(healthField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Pet", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String species = speciesField.getText().trim();
            String ageStr = ageField.getText().trim();
            String health = healthField.getText().trim();

            if (name.isEmpty() || species.isEmpty() || ageStr.isEmpty() || health.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int age = Integer.parseInt(ageStr);
                if (age < 0) throw new NumberFormatException();

                Pet newPet = new Pet(species, name, age, health);
                if (manager.addPet(newPet)) {
                    refreshPetList(manager.getAllPets());
                    updateSpeciesFilter();
                    JOptionPane.showMessageDialog(this, "Pet added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Pet with this name and species already exists.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid age format. Age must be a non-negative number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditPetDialog(Pet petToEdit) {
        if (petToEdit == null) return;

        JTextField nameField = new JTextField(petToEdit.getName(), 15);
        nameField.setEditable(false);
        JTextField speciesField = new JTextField(petToEdit.getSpecies(), 15);
        speciesField.setEditable(false);
        JTextField ageField = new JTextField(String.valueOf(petToEdit.getAge()), 5);
        JTextField healthField = new JTextField(petToEdit.getHealthStatus(), 15);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name (fixed):"));
        panel.add(nameField);
        panel.add(new JLabel("Species (fixed):"));
        panel.add(speciesField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Health Status:"));
        panel.add(healthField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Pet", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String ageStr = ageField.getText().trim();
            String health = healthField.getText().trim();

            if (ageStr.isEmpty() || health.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Age and Health Status cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int age = Integer.parseInt(ageStr);
                if (age < 0) throw new NumberFormatException();

                petToEdit.setAge(age);
                petToEdit.setHealthStatus(health);
                refreshPetList(manager.getAllPets()); // To update display
                JOptionPane.showMessageDialog(this, "Pet updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid age format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedPet() {
        Pet selectedPet = petList.getSelectedValue();
        if (selectedPet == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + selectedPet.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (manager.removePet(selectedPet)) {
                refreshPetList(manager.getAllPets());
                refreshAdopterList(manager.getAllAdopters());
                updateSpeciesFilter();
                JOptionPane.showMessageDialog(this, "Pet deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete pet.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterAndRefreshPets() {
        String selectedSpecies = (String) speciesFilterComboBox.getSelectedItem();
        String minAgeStr = ageFilterMinField.getText().trim();
        String maxAgeStr = ageFilterMaxField.getText().trim();

        List<Pet> filteredPets = manager.getAllPets();

        if (selectedSpecies != null && !selectedSpecies.equals("All")) {
            filteredPets = filteredPets.stream().filter(p -> p.getSpecies().equalsIgnoreCase(selectedSpecies)).collect(Collectors.toList());
        }

        try {
            if (!minAgeStr.isEmpty()) {
                int minAge = Integer.parseInt(minAgeStr);
                if (minAge < 0) throw new NumberFormatException("Min age cannot be negative.");
                filteredPets = filteredPets.stream().filter(p -> p.getAge() >= minAge).collect(Collectors.toList());
            }
            if (!maxAgeStr.isEmpty()) {
                int maxAge = Integer.parseInt(maxAgeStr);
                if (maxAge < 0) throw new NumberFormatException("Max age cannot be negative.");
                filteredPets = filteredPets.stream().filter(p -> p.getAge() <= maxAge).collect(Collectors.toList());
            }
            refreshPetList(filteredPets);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid age for filtering: " + e.getMessage(), "Filter Error", JOptionPane.ERROR_MESSAGE);
            refreshPetList(manager.getAllPets());
        }
    }

    private void searchAndRefreshPets() {
        String searchTerm = searchPetField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            refreshPetList(manager.getAllPets());
            return;
        }
        List<Pet> searchResults = manager.getAllPets().stream().filter(p -> p.getName().toLowerCase().contains(searchTerm)).collect(Collectors.toList());
        refreshPetList(searchResults);
        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No pets found with that name.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showAddAdopterDialog() {
        JTextField nameField = new JTextField(15);
        JTextField phoneField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Adopter", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Phone are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Adopter newAdopter = new Adopter(name, phone);
            if (manager.addAdopter(newAdopter)) {
                refreshAdopterList(manager.getAllAdopters());
                JOptionPane.showMessageDialog(this, "Adopter added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Adopter with this name already exists.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditAdopterDialog(Adopter adopterToEdit) {
        if (adopterToEdit == null) return;

        JTextField nameField = new JTextField(adopterToEdit.getName(), 15);
        nameField.setEditable(false); // Name is key, don't allow edit easily
        JTextField phoneField = new JTextField(adopterToEdit.getPhone(), 15);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name (fixed):"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Adopter", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String phone = phoneField.getText().trim();
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            adopterToEdit.setPhone(phone);
            refreshAdopterList(manager.getAllAdopters());
            JOptionPane.showMessageDialog(this, "Adopter updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSelectedAdopter() {
        Adopter selectedAdopter = adopterList.getSelectedValue();
        if (selectedAdopter == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + selectedAdopter.getName() + "?\nThis will also mark their adopted pets as available.", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (manager.removeAdopter(selectedAdopter)) {
                refreshAdopterList(manager.getAllAdopters());
                refreshPetList(manager.getAllPets()); // Pets' adoption status might have changed
                JOptionPane.showMessageDialog(this, "Adopter deleted. Associated pets are now available.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete adopter.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchAndRefreshAdopters() {
        String searchTerm = searchAdopterField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            refreshAdopterList(manager.getAllAdopters());
            return;
        }
        List<Adopter> searchResults = manager.getAllAdopters().stream().filter(a -> a.getName().toLowerCase().contains(searchTerm)).collect(Collectors.toList());
        refreshAdopterList(searchResults);
        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No adopters found with that name.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showAdoptPetDialog(Pet petToAdopt) {
        if (petToAdopt == null || petToAdopt.getAdopterName() != null) {
            JOptionPane.showMessageDialog(this, "Pet not selected or already adopted.", "Adoption Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Adopter> availableAdopters = manager.getAllAdopters();
        if (availableAdopters.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No adopters available. Please add an adopter first.", "Adoption Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Adopter[] adopterArray = availableAdopters.toArray(new Adopter[0]);
        Adopter selectedAdopter = (Adopter) JOptionPane.showInputDialog(this, "Select an adopter for " + petToAdopt.getName() + ":", "Adopt Pet", JOptionPane.PLAIN_MESSAGE, null, adopterArray, adopterArray[0]);

        if (selectedAdopter != null) {
            if (manager.adoptPet(petToAdopt, selectedAdopter)) {
                refreshPetList(manager.getAllPets());
                refreshAdopterList(manager.getAllAdopters());
                JOptionPane.showMessageDialog(this, petToAdopt.getName() + " adopted by " + selectedAdopter.getName(), "Adoption Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Adoption failed. Pet might already be adopted.", "Adoption Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void returnSelectedPet(Pet petToReturn) {
        if (petToReturn == null || petToReturn.getAdopterName() == null) {
            JOptionPane.showMessageDialog(this, "Pet not selected or not currently adopted.", "Return Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to return " + petToReturn.getName() + " to the shelter?", "Confirm Return", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (manager.returnPetToShelter(petToReturn)) {
                refreshPetList(manager.getAllPets());
                refreshAdopterList(manager.getAllAdopters());
                JOptionPane.showMessageDialog(this, petToReturn.getName() + " has been returned to the shelter.", "Return Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to return pet.", "Return Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}