import com.apogames.kitchenchef.ai.Cooking;
import com.apogames.kitchenchef.ai.KitchenInformation;
import com.apogames.kitchenchef.ai.KitchenPlayerAI;
import com.apogames.kitchenchef.ai.Student;
import com.apogames.kitchenchef.ai.action.Action;
import com.apogames.kitchenchef.ai.actionPoints.ActionPoint;
import com.apogames.kitchenchef.ai.player.Player;
import com.apogames.kitchenchef.game.entity.Vector;
import com.apogames.kitchenchef.game.enums.*;
import com.apogames.kitchenchef.game.pathfinding.PathResult;
import com.apogames.kitchenchef.game.recipe.Recipe;

import java.util.*;

@Student(
        author = "Tim Wernecke",
        matrikelnummer = 169971L
)

/*

Uni-Projekt zur Zulassung für Algorithmen und Datenstrukturen (Semester 3)

*/



//TODO NEXT: Essen kochen
//TODO Add Single Player support: crash at
//TODO Wenn nach Zutatennehmen anzahl der Zutaten geringer ist für jedes Rezept, variable buy food auf true setzen
public class MultiBlaBlaCar extends KitchenPlayerAI {
    private static final List<KitchenIngredient> neededIngredients;
    private static final List<KitchenSpice> neededSpices;
    private static final List<List<KitchenIngredient>> ingredientsListList;
    private static final List<List<KitchenSpice>> spiceListList;
    private static final List<Recipe> currentRecipes;
    private static final boolean[] isCooking;
    private static ActionPoint dishTakingPoint;
    private static ActionPoint dishWashingPoint;

    static {
        dishWashingPoint = null;
        dishTakingPoint = null;
        isCooking = new boolean[]{false, false, false};
        spiceListList = new ArrayList<>();
        ingredientsListList = new ArrayList<>();
        neededSpices = new ArrayList<>();
        neededIngredients = new ArrayList<>();
        currentRecipes = new ArrayList<>();
    }

    private final List<ActionPoint> playerDestinations = new ArrayList<>();
    private ArrayList<ActionPoint> cuttingStations = new ArrayList<>();
    private ArrayList<ActionPoint> cookingStations = new ArrayList<>();
    private KitchenInformation information;

    @Override
    public String getName() {
        return "Bla Bla Car";
    }

    @Override
    public void update(KitchenInformation kitchenInformation, List<Player> players) {
        this.information = kitchenInformation;
        cuttingStations = getAllActionPointsFromEnum(KitchenActionPointEnum.CUTTING);
        cookingStations = getAllActionPointsFromEnum(KitchenActionPointEnum.COOKING);
        //THIS SHOULD ONLY HAPPEN ONCE AT THE START OF THE PROGRAMM
        if (playerDestinations.size() < players.size()) {
            System.out.println("start");
            for (int i = 0; i < players.size(); i++) {
                playerDestinations.add(null);
            }
            dishWashingPoint = this.getActionPointFromEnum(KitchenActionPointEnum.DISH_WASHING, 0);
            dishTakingPoint = this.getActionPointFromEnum(KitchenActionPointEnum.DISH_TAKING, 0);

            for (ActionPoint aPoint : this.information.getActionPoints()) {
                if (aPoint.getContent() == KitchenActionPointEnum.INGREDIENT_TAKE) {
                    ingredientsListList.add(aPoint.getIngredients());
                } else if (aPoint.getContent() == KitchenActionPointEnum.SPICE_TAKE) {
                    spiceListList.add(aPoint.getSpices());
                }
            }
            System.out.println(Arrays.toString(ingredientsListList.toArray()));
            System.out.println(Arrays.toString(spiceListList.toArray()));
        }

        //Logic Loop for all Players
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            Cooking task = p.getCooking();

            //Check if Player has order
            if (task == null) {
                this.noTask(players, p, i);
            } else {
                this.gotTask(players, task, p, i);
            }
        }
    }

    public void noTask(List<Player> players, Player p, int i) {
        //Get new Order
        ActionPoint nextCustomer = this.getOldestCustomer();
        if (nextCustomer != null) {
            if (isClosest(nextCustomer, p, players)) {
                goToActionPoint(nextCustomer, p, i);
            }
        } else {
            if (isClosest(dishWashingPoint, p, players)) {
                goToActionPoint(dishWashingPoint, p, i);
            } else {
                onlyGo(getActionPointFromEnum(KitchenActionPointEnum.CUSTOMER, 2), p, i);
            }
        }
    }

    public void gotTask(List<Player> players, Cooking task, Player p, int i) {
        final CookingStatus cookingStatus = task.getStatus();

        if (cookingStatus.equals(CookingStatus.NEEDED_DISH)) {
            if (isDestinationInUse(dishTakingPoint, i)) {
                this.goToActionPoint(dishTakingPoint, p, i);
            } else {
                onlyGo(dishTakingPoint, p, i);
            }
        } else if (cookingStatus.equals(CookingStatus.DISH)) {
            //Check if there are still enough Ingredients and Spices
            if (validIngredients(task.getIngredients()).size() < 1 || validSpices(task.getSpice()).size() < 1) {
                //Buy new Stuff
                this.buyNewStuff(p, i);
            } else {
                if (trueList(task.getIngredientsCorrect())) {
                    List<Integer> validIngredients = validIngredients(task.getIngredients());
                    List<Integer> validSpices = validSpices(task.getSpice());
                    List<Integer> overLaps = getOverlaps(validIngredients, validSpices);
                    float minDistance = Integer.MAX_VALUE;
                    ActionPoint smallestDistancePoint = null;
                    for (int tmp : overLaps) {
                        ActionPoint tmpPoint = getActionPointFromEnum(KitchenActionPointEnum.INGREDIENT_TAKE, tmp);
                        if (tmpPoint != null && tmpPoint.getPosition().distance(p.getPosition()) < minDistance) {
                            minDistance = tmpPoint.getPosition().distance(p.getPosition());
                            smallestDistancePoint = tmpPoint;
                        }
                    }
                    if (smallestDistancePoint != null) {
                        goToActionPoint(smallestDistancePoint, p, i);
                    }
                }
            }
        } else if (cookingStatus.equals(CookingStatus.RAW)) {
            List<Integer> validIngredients = null, validSpices = null;
            if (trueList(task.getIngredientsCorrect())) {
                validIngredients = validIngredients(task.getIngredients());
            }
            if (trueList(task.getSpiceCorrect())) {
                validSpices = validSpices(task.getSpice());
            }
            if (validIngredients != null && validSpices != null) {
                List<Integer> overLaps = getOverlaps(validIngredients, validSpices);
                float minDistance = Integer.MAX_VALUE;
                ActionPoint smallestDistancePoint = null;
                for (int tmp : overLaps) {
                    ActionPoint tmpPoint = getActionPointFromEnum(KitchenActionPointEnum.INGREDIENT_TAKE, tmp);
                    if (tmpPoint != null && tmpPoint.getPosition().distance(p.getPosition()) < minDistance) {
                        minDistance = tmpPoint.getPosition().distance(p.getPosition());
                        smallestDistancePoint = tmpPoint;
                    }
                }
                if (smallestDistancePoint != null) {
                    goToActionPoint(smallestDistancePoint, p, i);
                }
                /*
                Distance zwischen jeder Überlappung und dem Spieler vergleichen
                 */
            } else if (validIngredients != null) {
                float minDistance = Integer.MAX_VALUE;
                ActionPoint smallestDistancePoint = null;
                for (int tmp : validIngredients) {
                    ActionPoint tmpPoint = getActionPointFromEnum(KitchenActionPointEnum.INGREDIENT_TAKE, tmp);
                    if (tmpPoint != null && tmpPoint.getPosition().distance(p.getPosition()) < minDistance && isDestinationInUse(tmpPoint, i)) {
                        minDistance = tmpPoint.getPosition().distance(p.getPosition());
                        smallestDistancePoint = tmpPoint;
                    }
                }
                if (smallestDistancePoint != null) {
                    goToActionPoint(smallestDistancePoint, p, i);
                }
            } else if (validSpices != null) {
                float minDistance = Integer.MAX_VALUE;
                ActionPoint smallestDistancePoint = null;
                for (int tmp : validSpices) {
                    ActionPoint tmpPoint = getActionPointFromEnum(KitchenActionPointEnum.SPICE_TAKE, tmp);
                    if (tmpPoint != null && tmpPoint.getPosition().distance(p.getPosition()) < minDistance && isDestinationInUse(tmpPoint, i)) {
                        minDistance = tmpPoint.getPosition().distance(p.getPosition());
                        smallestDistancePoint = tmpPoint;
                    }
                }
                if (smallestDistancePoint != null) {
                    goToActionPoint(smallestDistancePoint, p, i);
                }
            }

        } else if (cookingStatus.equals(CookingStatus.READY_FOR_CUTTING)) {
            //Man könnte die cuttingStations noch nach der distanz zum spieler sortieren
            for (ActionPoint cut : cuttingStations) {
                if (isDestinationInUse(cut, i)) {
                    goToActionPoint(cut, p, i);
                    break;
                }
            }
        } else if (cookingStatus.equals(CookingStatus.READY_FOR_COOKING)) {
            //BIN VERWIRRT WO IST DER UNTERSCHIED ZWISCHEN READY FOR CUTTING UND CUTTING ??? im tutorial steht davon nix also ignoriere ich es
            for (ActionPoint cut : cookingStations) {
                if (isDestinationInUse(cut, i)) {
                    goToActionPoint(cut, p, i);
                    break;
                }
            }
        } else if (cookingStatus.equals(CookingStatus.SERVEABLE)) {
            //Go to customer position
            ActionPoint customer = getActionPointFromPosition(task.getCustomerPosition());
            if (customer != null) {
                goToActionPoint(customer, p, i);
            } else {
                System.out.println(task.getCustomerPosition());
                System.out.println("Fatal Error: Worker cant get position of customer");
            }
        }
    }

    /**
     * gets closest ActionPoint to position
     *
     * @param position -> Vector of the position to get the ActionPoint from
     * @return returns the closest ActionPoint, returns null if position is null
     */
    private ActionPoint getActionPointFromPosition(Vector position) {
        if (position == null) {
            return null;
        }
        float closest = Float.MAX_VALUE;
        ActionPoint positionActionPoint = null;

        for (ActionPoint point : this.information.getActionPoints()) {
            if (position.distance(point.getPosition()) < closest) {
                closest = position.distance(point.getPosition());
                positionActionPoint = point;
            }
        }
        return positionActionPoint;
    }

    /**
     * Buys new ingredients and spices
     *
     * @param p        -> Player
     * @param playerId -> Id of Player
     */
    private void buyNewStuff(Player p, int playerId) {
        //Go To
        goToActionPoint(getActionPointFromEnum(KitchenActionPointEnum.BUY, 0), p, playerId);
        //ingredientsListList =
    }

    /**
     * @param p        -> Current Player to start cooking with
     * @param playerId -> unique PlayerId to use List<ActionPoint> playerDestinations
     */
    private void startCooking(Player p, int playerId) {
        //TODO: Finish function
        int cookingPointCounter = 0;
        for (ActionPoint point : this.information.getActionPoints()) {
            if (point.getContent() == KitchenActionPointEnum.COOKING) {
                if (!playerDestinations.contains(point)) {
                    playerDestinations.set(playerId, point);
                }

                if (point.isPlayerIn(p)) {
                    p.setAction(Action.use());
                    isCooking[playerId] = true;
                    return;
                }

                PathResult path = this.information.getWays().findWayFromTo(information, p, point.getPosition());
                p.setAction(Action.move(path.getMovement()));
                cookingPointCounter++;
            }
        }
    }

    /**
     * @param a -> List One
     * @param b -> List Two
     * @return returns every element that is in a && b
     */
    private List<Integer> getOverlaps(List<Integer> a, List<Integer> b) {
        if (b.size() > a.size()) {
            return getOverlaps(b, a);
        }
        List<Integer> overlaps = new ArrayList<>();
        for (Integer integer : a) {
            if (b.contains(integer)) {
                overlaps.add(integer);
            }
        }
        return overlaps;
    }

    /**
     * @param listOfIngredients -> List of needed Ingredients
     * @return returns a List of valid ActionPoint Numbers for the given List of Ingredients
     */
    private List<Integer> validIngredients(List<KitchenIngredient> listOfIngredients) {
        List<Integer> validPointNumbers = new ArrayList<>();
        for (int i = 0; i < ingredientsListList.size(); i++) {
            if (gotIngredients(ingredientsListList.get(i), listOfIngredients)) {
                validPointNumbers.add(i);
            }
        }
        return validPointNumbers;
    }

    /**
     * @param listOfSpices -> List of needed Spices
     * @return returns a List of valid ActionPoint Numbers for the given List of Spices
     */
    private List<Integer> validSpices(List<KitchenSpice> listOfSpices) {
        List<Integer> validPointNumbers = new ArrayList<>();
        for (int i = 0; i < spiceListList.size(); i++) {
            if (gotSpices(spiceListList.get(i), listOfSpices)) {
                validPointNumbers.add(i);
            }
        }
        return validPointNumbers;
    }

    /**
     * Removes used items from ingredient and spice list
     *
     * @param recipe -> recipe containing ingredients and spices
     * @param i      -> Number of List in ingredientsListList and spiceListList to remove items
     */
    private void removeItems(Recipe recipe, int i) {
        for (KitchenIngredient ingredient : recipe.getNeededIngredients()) {
            ingredientsListList.get(i).remove(ingredient);
        }
        for (KitchenSpice spice : recipe.getNeededSpice()) {
            spiceListList.get(i).remove(spice);
        }
    }

    /**
     * @param type  -> Type of ActionPoint
     * @param point -> ActionPoint to get number
     * @return -> returns the room number of the specified type, returns -1 if the room is not in information.getActionPoints()
     */
    private int getNumberOfRoom(KitchenActionPointEnum type, ActionPoint point) {
        int counter = 0;
        for (ActionPoint p : this.information.getActionPoints()) {
            if (p.getContent() == type && point != p) {
                counter++;
            } else if (point == p) {
                return counter;
            }
        }
        return -1;
    }

    private ActionPoint closestKitchenStuffDingsDa(Player p, List<Integer> validPoints) {
        float distance = Integer.MAX_VALUE;
        ActionPoint shortestDistance = null;
        int counter = 0;
        for (ActionPoint point : this.information.getActionPoints()) {
            if (point.getPosition().distance(p.getPosition()) < distance && validPoints.contains(counter)) {
                distance = point.getPosition().distance(p.getPosition());
                shortestDistance = point;
            }
            counter++;
        }
        return shortestDistance;
    }

    private List<Integer> pointsGotEverything(List<List<KitchenIngredient>> kitchenIngredientsList, List<List<KitchenSpice>> kitchenSpicesList, List<KitchenIngredient> ingredientsNeeded, List<KitchenSpice> spicesNeeded) {
        List<Integer> validPoints = new ArrayList<>();
        for (int i = 0; i < kitchenIngredientsList.size(); i++) {
            if (gotIngredients(kitchenIngredientsList.get(i), ingredientsNeeded) && gotSpices(kitchenSpicesList.get(i), spicesNeeded)) {
                validPoints.add(i);
            }
        }
        return validPoints;
    }

    /**
     * @param availableIngredients -> Currently available ingredients
     * @param neededIngredients    -> Needed ingredients for the next order
     * @return returns if there are enough ingredients in availableIngredients for the next order
     */
    private boolean gotIngredients(List<KitchenIngredient> availableIngredients, List<KitchenIngredient> neededIngredients) {
        for (KitchenIngredient ingredient : neededIngredients) {
            if (Collections.frequency(neededIngredients, ingredient) >= Collections.frequency(availableIngredients, ingredient)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param availableSpices -> Currently available spices
     * @param neededSpices    -> Needed spices for the next order
     * @return returns if there are enough spices in availableSpices for the next order
     */
    private boolean gotSpices(List<KitchenSpice> availableSpices, List<KitchenSpice> neededSpices) {
        for (KitchenSpice spice : neededSpices) {
            if (Collections.frequency(neededSpices, spice) > Collections.frequency(availableSpices, spice)) {
                return false;
            }
        }
        return true;
    }

/*private boolean gotIngredients(List<KitchenIngredient> availableIngredients, List<KitchenIngredient> neededIngredients) {
            for (KitchenIngredient ingredient : neededIngredients) {
                if (Collections.frequency(neededIngredients, ingredient) > Collections.frequency(availableIngredients, ingredient)) {
                    return false;
                }
            }
            return true;
        }

        private boolean gotSpices(List<KitchenSpice> availableSpices, List<KitchenSpice> neededSpices) {
            for (KitchenSpice spice : neededSpices) {
                if (Collections.frequency(neededSpices, spice) > Collections.frequency(availableSpices, spice)) {
                    return false;
                }
            }
            return true;
        }
    private int roomHasEverything(KitchenActionPointEnum type, int i, Recipe recipe) {
        int counter = 0;
        if ((type != KitchenActionPointEnum.SPICE_TAKE) && (type != KitchenActionPointEnum.INGREDIENT_TAKE)) {
            //Falschen Type übergeben
            return -1;
        }
        for (ActionPoint point : this.information.getActionPoints()) {
            if (point.getContent() == type) {
                if (counter == i) {
                    if (type == KitchenActionPointEnum.SPICE_TAKE) {

                    }
                } else {
                    counter++;
                }
            }
        }
    }
/*
    private ActionPoint getClosest(KitchenActionPointEnum type, ActionPoint point){
        float minDistance = Float.MAX_VALUE;
        ActionPoint closestPoint = null;
        for(ActionPoint p : this.information.getActionPoints()){
            if(point.getPosition().distance(p.getPosition()) < minDistance){
                minDistance = point.getPosition().distance(p.getPosition());
                closestPoint = p;
            }
        }
        return closestPoint;
    }
*/
/*

    private int getNextRoom() {
        List<Integer> availableRooms = new ArrayList<>();
        int counter = 0;
        for (ActionPoint point : this.information.getActionPoints()) {
            if (point.getContent() == KitchenActionPointEnum.INGREDIENT_TAKE) {

                if(point.getIngredients())

                counter++;
            }
        }
    }
*/

    /**
     * @param booleanList -> List of booleans
     * @return returns true if the complete list is true
     */
    private boolean trueList(List<Boolean> booleanList) {
        return !booleanList.isEmpty() && Collections.frequency(booleanList, true) != booleanList.size();
    }

    private void sumEverything(Recipe recipe) {
        neededIngredients.addAll(recipe.getNeededIngredients());
        neededSpices.addAll(recipe.getNeededSpice());
    }

    private void removeRecipe(Recipe recipe) {
        List<KitchenIngredient> recipeIngredients = recipe.getNeededIngredients();
        List<KitchenSpice> recipeSpice = recipe.getNeededSpice();

        for (int i = 0; i < Math.max(recipeIngredients.size(), recipeSpice.size()); i++) {
            if (i < recipeIngredients.size()) {
                neededIngredients.remove(recipeIngredients.get(i));
            }
            if (i < recipeSpice.size()) {
                neededSpices.remove(recipeSpice.get(i));
            }
        }
    }

    /**
     * @param point    -> the point that we want to check
     * @param playerId -> the current player
     * @return returns true if the destination is already being used / targeted by another player
     */
    private boolean isDestinationInUse(ActionPoint point, int playerId) {
        for (int i = 0; i < playerDestinations.size(); i++) {
            if (i != playerId && playerDestinations.get(i) == point) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param point   -> relevant action point
     * @param p       -> current player
     * @param players -> a list of all players
     * @return returns true if the player p is the closest currently not busy player
     */
    private boolean isClosest(ActionPoint point, Player p, List<Player> players) {
        float distanceP = point.getPosition().distance(p.getPosition());
        for (Player pp : players) {
            if (point.getPosition().distance(pp.getPosition()) < distanceP && pp.getCooking() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return returns true if used plates exist
     */
    private boolean gotUsedPlates() {
        return Objects.requireNonNull(this.getKitchenDish(KitchenActionPointEnum.DISH_WASHING)).size() > 0;
    }

    private List<KitchenDish> getKitchenDish(KitchenActionPointEnum type) {
        List<ActionPoint> points = this.information.getActionPoints();
        for (ActionPoint a : points) {
            if (a.getContent() == type) {
                return a.getDishes();
            }
        }
        return null;
    }

    /**
     * @param point    -> ActionPoint to go to and perform Action
     * @param p        -> Player to go to the ActionPoint
     * @param playerId -> Player ID
     */
    private void goToActionPoint(ActionPoint point, Player p, int playerId) {
        if (!playerDestinations.contains(point)) {
            playerDestinations.set(playerId, point);
        }

        if (point.isPlayerIn(p)) {
            p.setAction(Action.use());
            return;
        }

        PathResult path = this.information.getWays().findWayFromTo(information, p, point.getPosition());
        p.setAction(Action.move(path.getMovement()));
    }

    /**
     * //Equivalent to goToActionPoint(), except remove of usage of this ActionPoint
     *
     * @param point    -> ActionPoint to go to
     * @param p        -> Player to go to the ActionPoint
     * @param playerId -> Player ID
     */
    private void onlyGo(ActionPoint point, Player p, int playerId) {
        if (!playerDestinations.contains(point)) {
            playerDestinations.set(playerId, point);
        }
        if (!point.isPlayerIn(p)) {
            PathResult path = this.information.getWays().findWayFromTo(information, p, point.getPosition());
            p.setAction(Action.move(path.getMovement()));
        }
    }

    /**
     * @param type -> which enum type of point we need
     * @param i    -> which point we want (nr 1 nr 2 etc)
     * @return returns the ActionPoint from a given Type and a Index, returns null if the room does not exist
     */
    private ActionPoint getActionPointFromEnum(KitchenActionPointEnum type, int i) {
        int counter = 0;
        for (ActionPoint point : this.information.getActionPoints()) {
            if (point.getContent() == type) {
                if (counter != i) {
                    counter++;
                } else {
                    return point;
                }
            }
        }
        return null;
    }

    /**
     * @param type -> ActionPoint Type
     * @return returns ArrayList of all ActionPoint that are from KitchenActionPointEnum "type"
     */
    private ArrayList<ActionPoint> getAllActionPointsFromEnum(KitchenActionPointEnum type) {
        ArrayList<ActionPoint> allPoints = new ArrayList<>();
        for (ActionPoint point : information.getActionPoints()) {
            if (point.getContent() == type) {
                allPoints.add(point);
            }
        }
        return allPoints;
    }

    /**
     * @return returns the longest waiting customer
     */
    private ActionPoint getOldestCustomer() {
        //TODO: Check if player arrives before customer leaves
        ActionPoint newestCustomer = null;
        float longestWaitingTime = 0;
        for (ActionPoint point : this.information.getActionPoints()) {
            if (point.getContent() == KitchenActionPointEnum.CUSTOMER && !point.wasVisited() && point.isCustomerWaiting() && !playerDestinations.contains(point))
                if (point.getWaitingTime() > longestWaitingTime) {
                    newestCustomer = point;
                    longestWaitingTime = point.getWaitingTime();
                }
        }
        return newestCustomer;
    }
}