import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class restaurant {
    static String writeData;
    static File NewFile = new File("billingDetails.csv");
    public static void main(String[] args) {
        while (true) {
            getData menuListObject = new getData("menuList.csv", 3);
            getData billObj = new getData("billingDetails.csv", 5);
            String[][] menuData = menuListObject.totalData; 
            String[][] bilData = billObj.totalData; 
            MainMenu(); 
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            sc.nextLine();
            if(choice==1) {
                int[] dataEntered = dataEntry();
                menuListObject.showDetails(dataEntered, menuData); 
                String s = (billObj.lastDetails() + 1) + "," + date() + "," + menuListObject.totalBills + ","+writeData;//storing the data for writing in file

                char ch = sc.nextLine().charAt(0); 
                if (ch == 'y' || ch == 'Y') {
                    billObj.write(s, true); 
                } else {
                    billObj.write(s, false);
                }
            }
            else if(choice==2){

                System.out.println("enter bill id");
                int billNumb = sc.nextInt();
                sc.nextLine();
                billObj.overWrite(billNumb);
                writingFile(billObj.li,NewFile); 

            }
            else if (choice==3) {
                todayCollection(bilData); 
            }

                System.out.println("enter B to go back to home ");
                System.out.println("enter any key to exit");
                char mainMenuNav = sc.nextLine().charAt(0); 
                if(mainMenuNav=='B'|| mainMenuNav=='b'){
                    continue;
                }
                else{
                    break;
                }

        }

    }

    public static String date(){ 
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        String currentDate= myDateObj.format(myFormatObj);
        return currentDate;
    }
    public static void MainMenu(){ 
        System.out.println("Welcome to restaurant");
        System.out.println("1:Enter Order");
        System.out.println("2:Edit Bill");
        System.out.println("3:See Collection Of amount of the day\n");
        System.out.println("Please Enter the choice\n");
    }
    public static int[] dataEntry(){ 
        Scanner sc  = new Scanner(System.in);
        System.out.println("enter the quantity");
        int count = sc.nextInt();
        int[] dataEntered = new int[count * 2];
        int items = 0;
         writeData = "";
        for (int i = 0; i < count * 2; i += 2) {
            System.out.println("Enter menu of " + (items + 1) + " item");
            int MenuID = sc.nextInt();
            System.out.println("Enter quantity of" + (items + 1) + " item");
            int quantity = sc.nextInt();
            writeData += MenuID + " " + quantity + " ";
            dataEntered[i] = MenuID;
            dataEntered[i + 1] = quantity;
            items++;
        }
        return dataEntered;
    }
    public static void todayCollection(String[][] bilData){ 
        double totalCollection =0;
        String date = date();
        for(int i=0;i<bilData.length;i++){
                if(bilData[i][1].equals(date) && bilData[i][4].equals("approved")){
                    totalCollection+=Double.parseDouble(bilData[i][2]);
            }
        }
        System.out.println("collection on day "+date+" is");
        System.out.println(totalCollection+"Rs");

    }
    public static void writingFile(ArrayList<String> li,File NewFile){ 
        try {

            FileOutputStream FileWrite = new FileOutputStream("billingDetails.csv",
                    false);
            FileWrite.close();
            if (NewFile.exists()){
                System.out.println("Data Modified");
            }
            if (NewFile.createNewFile()) {
                System.out.println("Status updated in file");
            }
            FileOutputStream FileWrite2 = new FileOutputStream("billingDetails.csv",
                    true);

            for (String listString : li) {
                listString += "\n";
                byte[] ByteInput = listString.getBytes();
                FileWrite2.write(ByteInput);
            }
            FileWrite2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


class getData{
    String[][] totalData; 
    ArrayList<String> li = new ArrayList<>();
    Scanner sc ;
    double totalBills;
    String writeData;
    getData(String fileName,int count){
        this.totalBills=0;
        this.writeData="";
        try {
            File readFile = new File(fileName);
            sc = new Scanner(readFile);
            while (sc.hasNext()) {
                li.add(sc.nextLine());  
            }
            totalData = new String[li.size()][count]; 
            for (int i = 0; i < li.size(); i++) {
                String[] words = li.get(i).split(","); 
                for (int j = 0; j < count; j++) {
                    totalData[i][j] = words[j];
                }
            }
            sc.close();

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public int lastDetails(){ 
         if (li.size()==0) return 0;

         else {
             String s = totalData[li.size()-1][0];
             return Integer.valueOf(s);
         }
    }
    public void showDetails(int[] dataEntered,String[][] data){ 
        int count =0;
        String format = "|%1$-10s|%2$-40s|%3$-20s|%4$-30s|%5$-30s|\n";
        System.out.format(format,"item","         description        ","quantity","bill for 1 item","total bill");
        for (int i = 0; i < dataEntered.length; i+=2) {
            String totalBill = totalData[dataEntered[i]-1][2];
            double bill =Double.valueOf(totalBill);
            System.out.format(format,(count+1),"         "+ totalData[dataEntered[i]-1][1]+"       ",dataEntered[i+1],bill,bill*dataEntered[i+1]);
            this.totalBills = this.totalBills+bill*dataEntered[i+1];
            count++;
        }

        System.out.println("Total bill amount is    "+this.totalBills);
        System.out.println("Press Y to approve or Any Other Key to cancel");

    }
    public void write(String data,boolean approval){  
        try {
            FileOutputStream fo = new FileOutputStream("billingDetails.csv",true);
            if(approval==true) data=data+",approved\n";
            else{
                data=data+",canceled\n";
            }
            byte[] arr = data.getBytes();
            fo.write(arr);
            fo.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void overWrite(int billid){ 
        sc = new Scanner(System.in);
        for(int i=0;i<li.size();i++){
            String[] arr = li.get(i).split(",");
            if(Integer.parseInt(arr[0])==billid) {
                System.out.println(li.get(i));
                if (arr[4].equals("approved")) {
                    System.out.println("To cancel press 'C' ");
                    char ch = sc.next().charAt(0);
                    if (ch == 'c' || ch == 'C') {
                        arr[4] = "canceled";
                    }
                } else {
                    if (arr[4].equals("canceled")) {
                        System.out.println("To Approve press 'A' ");
                        char ch = sc.next().charAt(0);
                        if (ch == 'A' || ch == 'a') {
                            arr[4] = "approved";
                        }
                    }


                }
                String toWrite = String.join(",", arr);
                li.set(i, toWrite);
                break;

            }
        }

    }



}
