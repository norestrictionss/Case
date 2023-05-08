
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.lang.StringBuffer;
import java.util.HashMap;
import java.text.DecimalFormat;
import javax.swing.text.AbstractDocument.Content;

// Barış Giray Akman
//

public class Case {
    
    public static StringBuffer result = new StringBuffer(); // That variable holds the data fetched from URL.
    public static HashMap<String, Double> contracts_and_prices = new HashMap<>(); // That hashmap holds total price data with corresponding date. 
    public static HashMap<String, Double> contracts_and_quantities = new HashMap<>(); // That hashmap holds total quantity data with corresponding date. 

    // That function convert number into formatted manner.
    public static String convertToFormattedNumber(double number){
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String formattedNumber = formatter.format(number);
        return formattedNumber;
    }

    // That function converts our string including PH to date format as specified in the instructions.
    public static  String convertToDate(String date){

        try{
            String year ="20"+date.charAt(3)+date.charAt(4); 
            String month = ""+date.charAt(5)+date.charAt(6);
            String day = ""+date.charAt(7)+date.charAt(8);
            String hour = ""+date.charAt(9)+date.charAt(10);
            return day+"."+month+"."+year+" "+hour+":"+"00";
        }
        
        catch(Exception e){
            return date;
        }
        

        
    }
 
    

    // That function fetches the price, quantity ant dates from the array created in the main function.
    public static void assignElements(String result_array[]){

        ArrayList<String> features = new ArrayList<String>();
        for(int i =5;i<result_array.length;){

            
            if(i+4>result_array.length-1)
                return;

            // Those variables have been used to hold the values achieved from the array.
            double price= Float.parseFloat(result_array[i+3].split(":")[1]);
            int quantity = Integer.parseInt(result_array[i+4].split(":")[1]);
            String conract = result_array[i+2].split(":")[1];
            
            // According to formula given in the pdf file, values have been calculated.
            double operation_price = (1.0*price*quantity) / 10;
            double total_quantity = (quantity*1.0)/10;

            // If our date starts with PB, it won't be inserted to our hashmap. 

            // That variables checks if our contract is starting with PB.
            boolean isPB = conract.charAt(1)=='P' && conract.charAt(2)=='B';

            
            // If hashmap doesn't include that date, it will be inserted to our hashmap.
            // If our hashmap already includes that date, values will be added to corresponding value of hashmap's key.
            if(!contracts_and_prices.containsKey(conract) && !isPB){
                contracts_and_prices.put(conract, operation_price);
                contracts_and_quantities.put(conract, total_quantity);
            }
            else if(!isPB){
                contracts_and_prices.put(conract, contracts_and_prices.get(conract)+operation_price);
                contracts_and_quantities.put(conract, contracts_and_quantities.get(conract)+total_quantity);
            }
            
            i+=7;

        }


    }

    // That functions prints the results achieved.
    // Only two decimal after digits have been considered.
    public static void printResults(){

        System.out.printf("%-20s %-20s %-20s %-20s\n", "Tarih", "Toplam İşlem Miktari(MWh)", "Toplam İşlem Tutari(TL)", "Ağirlik Ortalama Fiyat(TL/MWh)");

        
        for(String i:contracts_and_prices.keySet()){
            
            
            double result = (int)(100*(contracts_and_prices.get(i)/contracts_and_quantities.get(i))); // That variable holds weighted sum.
            double total_quantity = (int)(contracts_and_quantities.get(i)*100);
            double total_price = (int)(contracts_and_prices.get(i)*100);

            total_quantity /=100;
            result /=100;
            total_price/=100;
            
            
            String date = convertToDate(i);
            // While printing, whole double variables are converted into formatted number.
            System.out.printf("%-25s %-25s %-25s %-25s\n", date, convertToFormattedNumber(total_quantity), convertToFormattedNumber(total_price), convertToFormattedNumber(result));
            
        }                                               
    }
    public static void main(String[] args){
       
        try{

            // URL connection provided.
            HttpURLConnection connection;
            BufferedReader reader;
            String line;
            URL url = new URL("https://seffaflik.epias.com.tr/transparency/service/market/intra-day-trade-history?endDate=2022-01-26&startDate=2022-01-26");
            connection = (HttpURLConnection) url.openConnection();

            // Adjustment of connection method
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);  


            
            // Status check
            int status = connection.getResponseCode();



            // If there is no specific error, program will be executed as normal.
            if(status<=299){

              
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
               
                while((line = reader.readLine()) != null)
                    result.append(line);


                reader.close();

                
                // Conversion of StringBuffer to string 
                String actual_result = result.toString();
                String result_array[]= actual_result.split("[,{}]");
                // Adding the elements to an arraylist


                
                assignElements(result_array);
                printResults();


            }
            // If URL connection is unsuccessful, code below will be performed.
            else{
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while((line = reader.readLine()) != null){
                    result.append(line);
                }
                
                reader.close();
            }
            
        }
        catch(Exception e){
            e.printStackTrace();

        }

        
    
    }
}
