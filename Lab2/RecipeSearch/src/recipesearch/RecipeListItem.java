package recipesearch;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.ait.dat215.lab2.Recipe;
import java.io.IOException;


public class RecipeListItem extends AnchorPane {
    private RecipeSearchController parentController;

    @FXML private Label foodLbl, tidLabel, prisLabel;
    @FXML private ImageView foodImageV;
    @FXML private ImageView kokImageV;
    @FXML private ImageView huvudingrediensImageV;
    @FXML private ImageView svarighetsgradImageV;
    @FXML private TextArea forklaring;

    public RecipeListItem(Recipe recipe, RecipeSearchController recipeSearchController){

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("recipe_listitem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.parentController = recipeSearchController;
        this.foodImageV.setImage(recipe.getFXImage());
        this.foodLbl.setText(recipe.getName());
        this.kokImageV.setImage(recipeSearchController.getCuisineImage(recipe.getCuisine()));
        this.huvudingrediensImageV.setImage(recipeSearchController.getIngredientImage(recipe.getMainIngredient()));
        this.svarighetsgradImageV.setImage(recipeSearchController.getDifficultyImage(recipe.getDifficulty()));
        this.tidLabel.setText("" + recipe.getTime() + " minuter");
        this.prisLabel.setText("" + recipe.getPrice() + " kronor");
        this.forklaring.setText(recipe.getDescription());

        super.setOnMouseClicked(event -> {
            parentController.openRecipeView(recipe);
        });
    }




}
