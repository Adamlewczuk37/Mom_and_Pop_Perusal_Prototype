import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class Yelp {
    public static void main(String[] args) {
        String filepath = "../json_dataset.txt"; //Reference created JSON file on Desktop
        StringBuilder stringBuilder = new StringBuilder();

        try (FileReader reader = new FileReader(filepath); //Create new file reader as a BufferedReader
            BufferedReader buffer = new BufferedReader(reader);
        ){
            String line = "";
            while ((line = buffer.readLine()) != null) {
               stringBuilder.append(line); //Read line-by-line  with the file reader and append to the StringBuilder
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String jsonInput = stringBuilder.toString(); //Convert file to a String

        RestaurantArray dataset = new RestaurantArray();
        dataset.fillList(jsonInput); //Create and load in a new dataset that stores in every restaurant
        RestaurantArray datasetCopy = new RestaurantArray(dataset); //Perform deep copy of the dataset

        Scanner reader = new Scanner(System.in); //Create reader to scan user inputs in command line
        String input = ""; //Initialize variables used for inputs in main code
        String endLoop = "";
        String stateInput = "";
        String cityInput = "";

        System.out.println( "Welcome to Mom & Pop Perusal!");
        
        while (!(endLoop.equals("f"))){ //Condition to end the while loop is setting endLoop to "f"
            System.out.print("Enter the state abbreviation that you would like to search in. Otherwise, enter 'n'. ");
            input = reader.next(); //Read in the input to filter by state abbreviation
            if (!(input.equals("n"))){
               stateInput = input;
            }

            System.out.print("Enter the city that you would like to search in. Otherwise, enter 'n'. ");
            input = reader.next(); //Read in the input to filter by city
            if (!(input.equals("n"))){
               cityInput = input;
            }

            ArrayList<Integer> numbers = new ArrayList<Integer>(); //Create 3 arrays: one for user input of numbers, one for 
                                                                   //attached restaurant categories, and one all selected categories
            ArrayList<String> selectedCuisines = new ArrayList<String>();
            ArrayList<String> intermediateCuisines = new ArrayList<String>();
            CuisineMap availableCuisines = new CuisineMap(); //Create and load hashmap that maps numbers to cuisines
            availableCuisines.LoadCuisines();
            availableCuisines.PrintCuisines(); //Print available options to the user
            int number = 0;

            System.out.print("Enter number(s) based on the cuisine(s) wanted above seperated by spaces. Otherwise, enter 'n'.\n");
            System.out.print("To exit, please enter 'x'. ");
            while (!(input.equals("x"))){
               input = reader.next(); //Capture sequence of numbers entered by the user
               if (input.equals("n")){ //Terminate user input immediately
                  break;
               }
               try {
                  number = Integer.parseInt(input);
                  numbers.add(number); //Add numbers to array if valid
               } catch(Exception e) {
                  break;
               }
            }

            if (!(input.equals("n"))){
               for (int i = 0; i < numbers.size(); i++){
                  number = numbers.get(i);
                  intermediateCuisines =  availableCuisines.getCuisines().get(number); //Find associated restaurant categories for each number
                  for (int j = 0; j < intermediateCuisines.size(); j++){
                     selectedCuisines.add(intermediateCuisines.get(j)); //Add all categories to main array
                  }
               }
            }

            boolean cont = false; //Boolean to skip extra conditions if an index was already flagged for removal
            int length = dataset.getRestaurants().size();
            ArrayList<Integer> indicesToRemove = new ArrayList<Integer>(); //Initialize array of indices to remove

            for (int i = 0; i < length; i++){
               boolean disjoint = Collections.disjoint(selectedCuisines, dataset.getRestaurants().get(i).getCategories()); //Find if selected categories and restaurant's categories don't match
               cont = false;
               if (!(stateInput.equals(""))){
                  if (!(dataset.getRestaurants().get(i).getState().equals(stateInput))){
                     indicesToRemove.add(i); //Remove element if state does not match up to selection
                     cont = true;
                  }
               }
               if ((!(cityInput.equals(""))) && (cont == false)){
                  if (!(dataset.getRestaurants().get(i).getCity().equals(cityInput))){
                     indicesToRemove.add(i); //Remove element if city does not match up to selection
                     cont = true;
                  }
               }
               if ((!(selectedCuisines.isEmpty())) && (cont == false)){
                  if (disjoint == true){
                     indicesToRemove.add(i); //Remove element if it does match with any selected cuisines
                  }
               }
            }

            length = indicesToRemove.size() - 1;
            for (int i = length; i >= 0; i--){ //Remove all flagged indices from the dataset
               int index = indicesToRemove.get(i);
               dataset.getRestaurants().remove(index);
            }

            RestaurantArray results = new RestaurantArray(); //Initialize RestaurantArray to display options

            if (dataset.getRestaurants().size() == 0){ //Skip loop if no restaurants are left
               System.out.println("No restaurants in the dataset remaining");
               System.out.println("Please add less to the filter or double-check your input");
               input = "2";
            } else {
               dataset.shuffle(results); //If there are available options, shuffle and print options
               results.printTenElements(0);
               System.out.print("\n");
            }

            while (!(input.equals("2"))){
               System.out.print("Enter '1' to reshuffle options. Enter '2' to finish search. ");
               input = reader.next(); //Capture user input
               if (input.equals("1")){ //Reshuffle and print
                  dataset.shuffle(results);
                  results.printTenElements(0);
                  System.out.print("\n");
               } else if ((!(input.equals("1"))) && (!(input.equals("2")))){ //Alert for invalid entry
                  System.out.println("Invalid entry. Please enter again.");
               }
            }

            System.out.print("Would you like to do another search? (y/n) "); //Option to either search again or terminate program
            input = reader.next();
            if (input.equals("y")){
               input = "";
               stateInput = "";
               cityInput = "";
               dataset.copy(datasetCopy); //Set dataset to the original through another deep copy
            } else {
               endLoop = "f";
            }
        }

        System.out.print("\n");
        System.out.print("Thanks for using Mom & Pop Perusal!");
        reader.close();
    }
}


class RestaurantArray {
   private ArrayList<Restaurant> restaurants; //Holds an ArrayList of Restaurants to function as the dataset


   public RestaurantArray(){
      restaurants = new ArrayList<>();
   }

   public RestaurantArray(RestaurantArray other) { //Deep copy constructor
        this.restaurants = new ArrayList<>();
        for (Restaurant restaurant : other.restaurants) {
            this.restaurants.add(new Restaurant(restaurant));
        }
    }

    public void copy(RestaurantArray other) { //Allows for deep copy outside of a constructor
        this.restaurants = new ArrayList<>();
        for (Restaurant restaurant : other.restaurants) {
            this.restaurants.add(new Restaurant(restaurant));
        }
    }

   public void fillList(String jsonFile){
      GsonBuilder builder = new GsonBuilder(); //Initialize Gson to convert data from JSON string to RestaurantArray
      builder.setPrettyPrinting(); 
      Gson gson = builder.create();

      Type listType = new TypeToken<ArrayList<Restaurant>>(){}.getType(); //Specify the type of this data structure
      ArrayList<Restaurant> restaurantList = gson.fromJson(jsonFile, listType); //Load data into a temporary ArrayList
      restaurants.addAll(restaurantList); //Copy all elements to ArrayList of this datatype
   }
   
   public ArrayList<Restaurant> getRestaurants() { 
      return restaurants; 
   }

   public void setRestaurants(Restaurant restaurant) { //Add Restaurants to the list
      restaurants.add(restaurant);
   }

   public void shuffle(RestaurantArray results){ //Shuffle results into another RestaurantArray based on random number generation
      results.getRestaurants().clear(); //Clear RestaurantArray to make room for new results
      Set<Integer> uniqueRandom = new HashSet<>(); //Initialize variables for function to operate
      Random random = new Random();
      int size = restaurants.size() + 1;

      while (uniqueRandom.size() < 10){ //Load 10 random numbers < ~ 46000 into the set
         int number = random.nextInt(size);
         uniqueRandom.add(number);
      }
      
      for (int randomNumber : uniqueRandom){
         Restaurant foundRestaurant = restaurants.get(randomNumber); //Load random elements from local ArrayList to new ArrayList
         results.setRestaurants(foundRestaurant);
      }
   }

   public void printTenElements(int ref){ //Nicely print 10 Restaurants from any starting index
      for (int i = ref; i < Math.min(ref + 10, restaurants.size()); i++){
         if (i >= ref){
            System.out.println(restaurants.get(i));
            System.out.println("\n");
         }
      }
   }
}

class Restaurant { //Data structure to load Yelp database in JSON format
   private String _id; //Each attribute for Restaurant has appropriate format that matches Yelp database
   private String business_id;
   private String name; 
   private String address;
   private String city;
   private String state;
   private String postal_code;
   private float latitude;
   private float longitude;
   private float stars;
   private int review_count;

   private ArrayList<String> categories;


   public Restaurant(){} 

   public Restaurant(Restaurant other) { //Deep copy constructor
        this._id = other._id;
        this.business_id = other.business_id;
        this.name = other.name;
        this.address = other.address;
        this.city = other.city;
        this.state = other.state;
        this.postal_code = other.postal_code;
        this.latitude = other.latitude;
        this.longitude = other.longitude;
        this.stars = other.stars;
        this.review_count = other.review_count;

        this.categories = new ArrayList<>(other.categories);
    }
   
   public String getID() { 
      return _id; 
   }
   
   public void setID(String ID) { 
      this._id = ID; 
   } 

   public String getBusinessID() { 
      return business_id; 
   }
   
   public void setBusinessID(String business_ID) { 
      this.business_id = business_ID; 
   } 
   
   public String getState() { 
      return state; 
   }
   
   public void setState(String state) { 
      this.state = state; 
   } 

   public String getAddress() { 
      return address; 
   }
   
   public void setAddress(String addr) { 
      this.address = addr; 
   } 

   public String getCity() { 
      return city; 
   }
   
   public void setCity(String city) { 
      this.city = city; 
   } 

   public String getPostal() { 
      return postal_code; 
   }
   
   public void setPostal(String PC) { 
      this.postal_code = PC; 
   } 

   public float getLatitude() { 
      return latitude; 
   }
   
   public void setLatitude(float latitude) { 
      this.latitude = latitude; 
   } 

   public float getLongitude() { 
      return longitude; 
   }
   
   public void setLongitude(float longitude) { 
      this.longitude = longitude; 
   }

   public float getStars() { 
      return stars; 
   }
   
   public void setStars(float star) { 
      this.stars = star; 
   }
   
   public int getReviewCount() { 
      return review_count; 
   }
   
   public void setAge(int review) { 
      this.review_count = review; 
   }

   public String getName() { 
      return name; 
   }
   
   public void setName(String name) { 
      this.name = name; 
   } 

   public ArrayList<String> getCategories() { 
      return categories; 
   }
   
   public void setCategories(ArrayList<String> categories) { 
      this.categories = categories; 
   } 
   
   public String toString() { //Convert to string to print out necessary information for each restaurant
      return "name: " + name + ", city: " + city + ", state: " + state + ", address: " + address + ", stars: " + stars + ", categories: " + categories; 
   }  
}

class CuisineMap { //Map of integers to cuisines/categories to support multiple user inputs
   private Map<Integer, ArrayList<String>> cuisines;


   public CuisineMap(){
      cuisines = new HashMap<>();
   }

   public Map<Integer, ArrayList<String>> getCuisines(){
      return cuisines;
   }

   public void LoadCuisines(){ //Load map with integers to list of one or more cuisines for user selection
      ArrayList<String> names = new ArrayList<String>();

      names = new ArrayList<String>();
      names.add("American (New)");
      cuisines.put(1, names);
      
      names = new ArrayList<String>(); //Some entries have multiple categories to avoid filtering out relevant options
      names.add("Italian");
      names.add("Pizza");
      cuisines.put(2, names);

      names = new ArrayList<String>();
      names.add("Bars");
      names.add("Breweries");
      names.add("Pubs");
      cuisines.put(3, names);

      names = new ArrayList<String>();
      names.add("Mexican");
      names.add("Latin American");
      cuisines.put(4, names);

      names = new ArrayList<String>();
      names.add("Chinese");
      cuisines.put(5, names);

      names = new ArrayList<String>();
      names.add("Japanese");
      cuisines.put(6, names);

      names = new ArrayList<String>();
      names.add("Korean");
      cuisines.put(7, names);

      names = new ArrayList<String>();
      names.add("Indian");
      cuisines.put(8, names);

      names = new ArrayList<String>();
      names.add("French");
      cuisines.put(9, names);

      names = new ArrayList<String>();
      names.add("Vietnamese");
      cuisines.put(10, names);

      names = new ArrayList<String>();
      names.add("Thai");
      cuisines.put(11, names);

      names = new ArrayList<String>();
      names.add("German");
      cuisines.put(12, names);

      names = new ArrayList<String>();
      names.add("Spanish");
      cuisines.put(13, names);

      names = new ArrayList<String>();
      names.add("British");
      cuisines.put(14, names);

      names = new ArrayList<String>();
      names.add("Russian");
      names.add("Polish");
      cuisines.put(15, names);

      names = new ArrayList<String>();
      names.add("Middle Eastern");
      cuisines.put(16, names);

      names = new ArrayList<String>();
      names.add("Seafood");
      cuisines.put(17, names);

      names = new ArrayList<String>();
      names.add("Coffee & Tea");
      names.add("Cafes");
      names.add("Bubble Tea");
      names.add("Bagels");
      cuisines.put(18, names);

      names = new ArrayList<String>();
      names.add("Bakeries");
      cuisines.put(19, names);
   }

   public void PrintCuisines(){ //Print numerical options for the user
      System.out.print("1: American \n");
      System.out.print("2: Italian & Pizza \n");
      System.out.print("3: Bars & Breweries & Gastropubs \n");
      System.out.print("4: Mexican & Latin American \n");
      System.out.print("5: Chinese \n");
      System.out.print("6: Japanese \n");
      System.out.print("7: Korean \n");
      System.out.print("8: Indian \n");
      System.out.print("9: French \n");
      System.out.print("10: Vietnamese \n");
      System.out.print("11: Thai \n");
      System.out.print("12: German \n");
      System.out.print("13: Spanish \n");
      System.out.print("14: British \n");
      System.out.print("15: Russian & Eastern European \n");
      System.out.print("16: Middle Eastern \n");
      System.out.print("17: Seafood \n");
      System.out.print("18: Coffee & Tea \n");
      System.out.print("19: Bakeries \n");
   }
}
