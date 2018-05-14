
package recipesearch;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import se.chalmers.ait.dat215.lab2.Recipe;
import se.chalmers.ait.dat215.lab2.RecipeDatabase;


public class RecipeSearchController implements Initializable {

    RecipeDatabase db = RecipeDatabase.getSharedInstance();
    @FXML
    private FlowPane resultatFlowPane;
    @FXML
    private ComboBox huvudingrediensCBox, kokCBox;
    @FXML
    private RadioButton allaRButton;
    @FXML
    private RadioButton lattRButton;
    @FXML
    private RadioButton mellanRButton;
    @FXML
    private RadioButton svarRButton;
    @FXML
    private Spinner<Integer> maxPrisSpinner;
    @FXML
    private Slider tidSlider;
    @FXML
    private Label tidLbl, receptLbl, tid2Lbl, pris2Lbl;
    @FXML
    private ImageView receptPicImageV, kok2ImageV, huvudIngridiens2ImageV, svarighet2ImageV, closeImage;
    @FXML
    private AnchorPane recipeDetailPane;
    @FXML
    private SplitPane searchPane;
    @FXML
    private TextArea sammanfattning, beskrivning, ingredienser;

    private Map<String, RecipeListItem> recipeListItemMap = new HashMap<String, RecipeListItem>();

    RecipeBackendController rbc = new RecipeBackendController();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        for (Recipe recipe : rbc.getRecipes()) {
            RecipeListItem recipeListItem = new RecipeListItem(recipe, this);
            recipeListItemMap.put(recipe.getName(), recipeListItem);
        }

        initHuvudCBox(huvudingrediensCBox);
        initKokCBox(kokCBox);
        initRadioButton(allaRButton, lattRButton, mellanRButton, svarRButton);
        initSpinner(
                maxPrisSpinner, 0, 100, 0, 1);
        initSlider(tidSlider, tidLbl);

        populateHuvudIngrediensComboBox();
        populateKokComboBox();

        updateRecipeList();

    }

    private void initHuvudCBox(ComboBox huvudingrediensCBox) {
        huvudingrediensCBox.getItems().addAll("Visa alla", "Kött", "Kyckling", "Fisk", "Vegetarisk");
        huvudingrediensCBox.getSelectionModel().select("Visa alla");
        huvudingrediensCBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                rbc.setMainIngredient(newValue);
                updateRecipeList();
            }
        });
    }

    private void initKokCBox(ComboBox kokCBox) {
        kokCBox.getItems().addAll("Visa alla", "Afrika", "Asien", "Frankrike", "Grekland", "Indien", "Sverige");
        kokCBox.getSelectionModel().select("Visa alla");
        kokCBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                rbc.setCuisine(newValue);
                updateRecipeList();
            }
        });
    }

    private void initRadioButton(RadioButton allaRButton, RadioButton lattRButton,
                                 RadioButton mellanRButton, RadioButton svarRButton) {

        ToggleGroup difficultyToggleGroup = new ToggleGroup();
        allaRButton.setToggleGroup(difficultyToggleGroup);
        lattRButton.setToggleGroup(difficultyToggleGroup);
        mellanRButton.setToggleGroup(difficultyToggleGroup);
        svarRButton.setToggleGroup(difficultyToggleGroup);

        allaRButton.setSelected(true);

        difficultyToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {

                if (difficultyToggleGroup.getSelectedToggle() != null) {
                    RadioButton selected = (RadioButton) difficultyToggleGroup.getSelectedToggle();
                    rbc.setDifficulty(selected.getText());
                    updateRecipeList();
                }
            }
        });
    }

    private void initSpinner(Spinner<Integer> spinner, int minValue, int maxValue, int initialValue, int amountToStepBy) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                minValue, maxValue, initialValue, amountToStepBy);

        spinner.setValueFactory(valueFactory);
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue observable, Integer oldValue, Integer newValue) {
                rbc.setMaxPrice(newValue);
                System.out.println(newValue);
                updateRecipeList();
            }
        });

        spinner.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    Integer value = Integer.valueOf(maxPrisSpinner.getEditor().getText());
                    rbc.setMaxPrice(value);
                    updateRecipeList();
                }
            }
        });
    }

    private void initSlider(Slider tidSlider, Label tidLbl) {
        tidSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue observable, Number oldValue, Number newValue) {
                if (newValue != null && !newValue.equals(oldValue) && !tidSlider.isValueChanging()) {
                    rbc.setMaxTime(newValue.intValue());
                    updateRecipeList();
                }
                if (newValue.intValue() % 10 == 0) {
                    tidLbl.setText("" + newValue.intValue() + " minuter");
                }
            }
        });

    }

    private void updateRecipeList() {
        resultatFlowPane.getChildren().clear();
        List<Recipe> list = rbc.getRecipes();
        for (Recipe aList : list) {
            resultatFlowPane.getChildren().add(recipeListItemMap.get(aList.getName()));
        }
    }

    private void populateRecipeDetailView(Recipe recipe) {
        receptLbl.setText(recipe.getName());
        receptPicImageV.setImage(recipe.getFXImage());
        kok2ImageV.setImage(getCuisineImage(recipe.getCuisine()));
        huvudIngridiens2ImageV.setImage(getIngredientImage(recipe.getMainIngredient()));
        tid2Lbl.setText(recipe.getTime() + " minuter");
        svarighet2ImageV.setImage(getDifficultyImage(recipe.getDifficulty()));
        pris2Lbl.setText(recipe.getPrice() + " kr");
        sammanfattning.setText(recipe.getDescription());
        beskrivning.setText(recipe.getInstruction());
        ingredienser.setText(textEditor(recipe.getIngredients(), recipe.getServings()));
    }

    @FXML
    public void closeRecipeView() {
        searchPane.toFront();
    }

    void openRecipeView(Recipe recipe) {
        populateRecipeDetailView(recipe);
        recipeDetailPane.toFront();
    }

    private void populateHuvudIngrediensComboBox() {
        Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> p) {

                return new ListCell<String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        setText(item);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            Image icon = null;
                            String iconPath;
                            try {
                                switch (item) {

                                    case "Kött":
                                        iconPath = "RecipeSearch/resources/icon_main_meat.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Fisk":
                                        iconPath = "RecipeSearch/resources/icon_main_fish.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Kyckling":
                                        iconPath = "RecipeSearch/resources/icon_main_chicken.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Vegetarisk":
                                        iconPath = "RecipeSearch/resources/icon_main_veg.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                }
                            } catch (NullPointerException ex) {
                                //This should never happen in this lab but could load a default image in case of a NullPointer
                            }
                            ImageView iconImageView = new ImageView(icon);
                            iconImageView.setFitHeight(12);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        };
        huvudingrediensCBox.setButtonCell(cellFactory.call(null));
        huvudingrediensCBox.setCellFactory(cellFactory);
    }

    private void populateKokComboBox() {
        Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> p) {

                return new ListCell<String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        setText(item);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            Image icon = null;
                            String iconPath;
                            try {
                                switch (item) {

                                    case "Afrika":
                                        iconPath = "RecipeSearch/resources/icon_flag_africa.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Asien":
                                        iconPath = "RecipeSearch/resources/icon_flag_asia.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Frankrike":
                                        iconPath = "RecipeSearch/resources/icon_flag_france.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Grekland":
                                        iconPath = "RecipeSearch/resources/icon_flag_greece.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Indien":
                                        iconPath = "RecipeSearch/resources/icon_flag_india.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Sverige":
                                        iconPath = "RecipeSearch/resources/icon_flag_sweden.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                }
                            } catch (NullPointerException ex) {
                                //This should never happen in this lab but could load a default image in case of a NullPointer
                            }
                            ImageView iconImageView = new ImageView(icon);
                            iconImageView.setFitHeight(12);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        };
        kokCBox.setButtonCell(cellFactory.call(null));
        kokCBox.setCellFactory(cellFactory);
    }

    public Image getCuisineImage(String cuisine) {
        String iconPath;
        switch (cuisine) {
            case "Sverige":
                iconPath = "RecipeSearch/resources/icon_flag_sweden.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Afrika":
                iconPath = "RecipeSearch/resources/icon_flag_africa.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Asien":
                iconPath = "RecipeSearch/resources/icon_flag_asia.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Grekland":
                iconPath = "RecipeSearch/resources/icon_flag_greece.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Frankrike":
                iconPath = "RecipeSearch/resources/icon_flag_france.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Indien":
                iconPath = "RecipeSearch/resources/icon_flag_india.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));


        }
        return null;
    }

    public Image getIngredientImage(String ingredient) {
        String iconPath;
        switch (ingredient) {
            case "Kött":
                iconPath = "RecipeSearch/resources/icon_main_beef.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Kyckling":
                iconPath = "RecipeSearch/resources/icon_main_chicken.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Fisk":
                iconPath = "RecipeSearch/resources/icon_main_fish.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Vegetarisk":
                iconPath = "RecipeSearch/resources/icon_main_veg.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
        }
        return null;
    }

    public Image getDifficultyImage(String difficulty) {
        String iconPath;
        switch (difficulty) {
            case "Svår":
                iconPath = "RecipeSearch/resources/icon_difficulty_hard.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Mellan":
                iconPath = "RecipeSearch/resources/icon_difficulty_medium.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Lätt":
                iconPath = "RecipeSearch/resources/icon_difficulty_easy.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
        }
        return null;
    }

    private <T> String textEditor (List <T> list, int portioner){
        StringBuilder sb = new StringBuilder();
        sb.append("Antal portioner " + portioner);
        sb.append("\n");
        sb.append("\n");
        for (int i = 0; i < list.size(); i++){
            sb.append("\t" + list.get(i));
            sb.append("\n");
        }
        return sb.toString();
    }

    @FXML
    public void closeButtonMouseEntered(){
        closeImage.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close_hover.png")));
    }

    @FXML
    public void closeButtonMousePressed(){
        closeImage.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close_pressed.png")));
    }

    @FXML
    public void closeButtonMouseExited(){
        closeImage.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close.png")));
    }

    @FXML
    public void mouseTrap(Event event){
        event.consume();
        closeRecipeView();
    }
}