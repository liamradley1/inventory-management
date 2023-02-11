package com.morgan.inventorymanagement;

import com.morgan.inventorymanagement.dao.ItemRepository;
import com.morgan.inventorymanagement.dao.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.System.exit;

@Service
@Slf4j
public class InventoryManagementService {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TransactionRepository transactionRepository;

    private final Scanner input = new Scanner(System.in);

    private ArrayList<Item> itemsList = new ArrayList<>();
    private ArrayList<Transaction> transactionsList = new ArrayList<>();

    public void runInventoryManagementService() throws IOException {    //declaring function
        itemsList = readItemsFromDb();
        transactionsList = readTransactionsFromDb();
        printPreamble();
//        itemsList = readItemsFromFile();
//        transactionsList = readTransactionsFromFile();

        int userInput = input.nextInt(); //defining value from userInput
        input.nextLine();
        switch (userInput) {
            case 1:
                Transaction transaction = addItem(itemsList);
                addTransaction(transaction, transactionsList);
//                writeToFiles(itemsList, transactionsList);
                writeToDb(itemsList, transactionsList);
                break;
            case 2:

                transaction = editQuantity(itemsList);
                addTransaction(transaction, transactionsList);
//                writeToFiles(itemsList, transactionsList);
                writeToDb(itemsList, transactionsList);
                break;
            case 3:
                transaction = removeItem(itemsList);
                addTransaction(transaction, transactionsList);
//                writeToFiles(itemsList, transactionsList);
                writeToDb(itemsList, transactionsList);
                break;
            case 4:
                viewTransactionReport(transactionsList);
                break;
            case 5:
                System.out.println("See ya chump");
                input.close();
                exit(0);
                break;
            default:
                System.err.println("This doesn't appear to be a valid option please try again!"); //s
                break;
        }
        runInventoryManagementService();
    }

    private void writeToDb(ArrayList<Item> itemsList, ArrayList<Transaction> transactionsList) {
        itemRepository.saveAll(itemsList);
        transactionRepository.saveAll(transactionsList);
    }

    private ArrayList<Item> readItemsFromDb() {
        ArrayList<Item> items = new ArrayList<>((Collection) itemRepository.findAll());
        return items;
    }

    private ArrayList<Transaction> readTransactionsFromDb() {
        ArrayList<Transaction> items = new ArrayList<>((Collection) transactionRepository.findAll());
        return items;
    }

    private  void viewTransactionReport(ArrayList<Transaction> transactionsList) {
		System.out.println("id,description,qtySold,Amount(£),remainingStock,transactionType,day");
		transactionsList.forEach(transaction -> {
			System.out.println(padId(transaction.getId().toString()) + "," + transaction.getDescription() + "," + transaction.getQtySold() + "," + transaction.getAmount() + "," +
					transaction.getRemainingStock() + "," + transaction.getTransactionType() + "," + transaction.getTimestamp());
		});
    }

    private  Transaction removeItem(ArrayList<Item> itemsList) {
        System.out.println("Enter the id of the item you wish to remove.");
        System.out.println("Id, description");
        int id;
        try {
            id = input.nextInt();
            input.nextLine();
            Item itemToDelete = itemsList.stream().filter(item -> item.getId() == id).collect(Collectors.toList()).get(0);
            Transaction transaction = new Transaction(itemToDelete.getId(), itemToDelete.getDescription(), itemToDelete.getQtyStock(),
                    itemToDelete.getTotalPrice(), 0, "Total sale of stock", Timestamp.from(Instant.now()));
            itemRepository.delete(itemToDelete);
            itemsList.remove(itemToDelete);
            return transaction;
        } catch (Exception e) {
            System.err.printf("Exception thrown: %s", e.getMessage());
            System.out.println("Returning to menu.");
        }
        return null;
    }

    private  Transaction editQuantity(ArrayList<Item> itemsList) {
        System.out.println("Enter the id of the item you wish to edit.");
        System.out.println("Id, description");
        itemsList.forEach(item -> System.out.println(item.getId() + " , " + item.getDescription()));
        int id;
        try {
            id = input.nextInt();
            input.nextLine();
            Item item = itemsList.stream().filter(item1 -> item1.getId() == id).collect(Collectors.toList()).get(0);
            System.out.println("Enter the quantity you wish to update to");
            int quantity = input.nextInt();
            input.nextLine();
            int difference = quantity - item.getQtyStock();
            item.setQtyStock(quantity);
            item.setTotalPrice(quantity * item.getUnitPrice());
            String transactionType = "No change";
            if (difference > 0) {
                transactionType = "Stock increase";
            } else if (difference < 0) {
                transactionType = "Stock sold";
            }
            Transaction transaction = new Transaction(item.getId(), item.getDescription(), difference, Math.abs(item.getUnitPrice() * difference),
                    item.getQtyStock(), transactionType, Timestamp.from(Instant.now()));
            return transaction;
        } catch (Exception e) {
            System.err.printf("Exception thrown: %s", e.getMessage());
            System.out.println("Returning to menu.");
        }
        return null;
    }

    private  void addTransaction(Transaction transaction, ArrayList<Transaction> transactionsList) {
        transactionsList.add(transaction);
    }

    private  void writeToFiles(ArrayList<Item> itemsList, ArrayList<Transaction> transactionsList) throws IOException {
        String itemFile = "C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\items.txt";
        String transactionFile = "C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\transactions.txt";
        FileWriter itemWriter = new FileWriter(itemFile, false);
        itemWriter.write("id,description,unitPrice,qtyStock,totalPrice,\n");
        itemsList.forEach(item -> {
            try {
                itemWriter.write(padId(item.getId().toString()) + "," + item.getDescription() + "," + item.getUnitPrice() + "," + item.getQtyStock() + "," + item.getTotalPrice() + ",\n");
            } catch (IOException e) {
                System.err.printf("IO Exception thrown %s", e.getMessage());
                exit(1);
            }
        });
        itemWriter.close();

        FileWriter transactionsWriter = new FileWriter(transactionFile, false);
        transactionsWriter.write("id,description,qtySold,Amount(£),remainingStock,transactionType,day,\n");
        transactionsList.forEach(transaction -> {
            try {
                transactionsWriter.write(padId(transaction.getId().toString()) + "," + transaction.getDescription() + "," + transaction.getQtySold() + "," + transaction.getAmount() + "," + transaction.getRemainingStock() + "," + transaction.getTransactionType() + "," + transaction.getTimestamp() + ",\n");
            } catch (IOException e) {
                System.err.printf("IO Exception thrown %s", e.getMessage());
                exit(1);
            }
        });
        transactionsWriter.close();
    }

    private Transaction addItem(ArrayList<Item> itemsList) {
        System.out.print("Please Enter a Description: ");
        String description = input.nextLine();  //description of item
        System.out.print("Please Enter Unit Price: ");
        double unitPrice = input.nextDouble();    //declaring userprice
        input.nextLine();
        System.out.print("Please Enter Quantity in stock: ");
        int qtyStock = input.nextInt(); //declaring stock amount
        input.nextLine();
        double totalPrice = unitPrice * qtyStock;  //arithmetic operation to find totalprice from 2 user inputs
        System.out.printf("The total price of this Item is: %f \n", totalPrice); //telling user total price

        int id = 1;
        try {
            id = itemsList.get(itemsList.size()-1).getId() + 1;
        } catch (Exception e) {
            System.out.println("This is the first entry");
        }
        Item item = new Item(id, description, unitPrice, qtyStock, totalPrice);
        itemsList.add(item);

        Transaction transaction = new Transaction(id, description, qtyStock, totalPrice, qtyStock, "Item added", Timestamp.from(Instant.now()));
        return transaction;
    }

    private  Item readStringToItemObject(String itemFile) {
        String[] itemComponents = StringUtils.commaDelimitedListToStringArray(itemFile);
        Item item = new Item(
                Integer.parseInt(itemComponents[0]),
                itemComponents[1],
                Double.parseDouble(itemComponents[2]),
                Integer.valueOf(itemComponents[3]),
                Double.parseDouble(itemComponents[4]));
        return item;
    }


    private  ArrayList<Item> readItemsFromFile() {
        File file = new File("C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\items.txt");
        List<String> lines = new ArrayList<>();
        try {
             lines = FileUtils.readLines(file, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.out.println("Error accessing items.txt: " + e.getMessage());
        }
//        for (String line : lines) {
//            System.out.println(line);
//        }
        // This prints the correct number
//        System.out.println("Size: " + lines.size());
        if (lines.size() != 0) {
			lines.remove(0);
		}
//        for(String line: lines) {
//            System.out.println(line);
//        }

        ArrayList<Item> items = new ArrayList<>();
		lines.forEach(line -> {
			Item item = readStringToItemObject(line);
//            System.out.println(item);
			items.add(item);
		});
//        for (Item item: items
//             ) {
//            System.out.println(item);
//        }
//        System.out.println("Size: " + items.size());
        return items;
//		String[] itemsFileLines = new String[0];
//		try {
//            String fileContents = FileUtils.readFileToString(new File("C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\items.txt"), StandardCharsets.UTF_8);
//			System.out.println(fileContents);
//            itemsFileLines = StringUtils.split(fileContents, ",\n");
//            for (String s: itemsFileLines
//                 ) {
//                System.out.println(s);
//            }
//
//        } catch (IOException e) {
//			System.err.println("Items.txt not found.");
//			exit(1);
//		}
//		ArrayList<String> itemFiles = new ArrayList<>(Arrays.asList(itemsFileLines));
//        log.info("Item files: {}", itemFiles);

//        for (String s: itemFiles
//             ) {
//            System.out.println(s);
//        }
//        System.out.println(itemFiles.size());


//        String[] items = new String[99999];
//        int itemCounter = 0;
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\items.txt"));
//            String itemString;
//            while((itemString = bufferedReader.readLine()) != null) {
//                System.out.println("count: " + itemCounter + 1 + " " + itemString);
//                items[itemCounter] = itemString;
//                ++itemCounter;
//            }
//            bufferedReader.close();
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        for (String item : items
//        ) {
//            Item itemToAdd = readStringToItemObject(item);
//            itemsList.add(itemToAdd);
//        return items;
    }

    private  Transaction readStringToTransactionObject(String transactionFile) {
//        System.out.println(transactionFile);
        String[] transactionComponents = StringUtils.commaDelimitedListToStringArray(transactionFile);
        Transaction transaction = new Transaction(Integer.parseInt(transactionComponents[0]),
                transactionComponents[1],
                Integer.parseInt(transactionComponents[2]),
                Double.parseDouble(transactionComponents[3]),
                Integer.parseInt(transactionComponents[4]),
                transactionComponents[5],
                Timestamp.valueOf(transactionComponents[6]));
        return transaction;
    }


    private  ArrayList<Transaction> readTransactionsFromFile() {
        File file = new File("C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\transactions.txt");
        List<String> lines = new ArrayList<>();
        try {
            lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Error accessing items.txt: " + e.getMessage());
        }
        if (lines.size() != 0) {
            lines.remove(0);
        }
//        for (String line: lines
//             ) {
//            System.out.println(line);
//        }
//        System.out.println("Lines: " + lines.size());
        ArrayList<Transaction> transactions = new ArrayList<>();
        lines.forEach(itemFile -> {
            Transaction transaction = readStringToTransactionObject(itemFile);
            transactions.add(transaction);
        });
//        transactions.forEach(transaction -> System.out.println(transaction));
        return transactions;
    }

    private  void printPreamble() {
        System.out.println("I N V E N T O R Y    M A N A G E M E N T    S Y S T E M");
        System.out.println("-----------------------------------------------");
        System.out.println("1. ADD NEW ITEM");
        System.out.println("2. UPDATE QUANTITY OF EXISTING ITEM");
        System.out.println("3. REMOVE ITEM");
        System.out.println("4. VIEW TRANSACTIONS REPORT");
        System.out.println("---------------------------------");
        System.out.println("5. Exit");
        System.out.print("\n Enter a choice and Press ENTER to continue[1-5]:");
    }

    private String padId(String id) {
        int lengthOfPadding = 5 - id.length();
        for(int i = 0; i < lengthOfPadding; i++) {
            id = "0" + id;
        }
        return id;
    }
}