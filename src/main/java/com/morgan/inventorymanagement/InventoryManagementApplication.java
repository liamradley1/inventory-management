package com.morgan.inventorymanagement;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.System.exit;

	@SpringBootApplication
	public class InventoryManagementApplication {

		@Autowired
		ItemRepository itemRepository;

		@Autowired
		TransactionRepository transactionRepository;

		private static final Scanner input = new Scanner(System.in);

		private static ArrayList<Item> itemsList = new ArrayList<>();
		private static ArrayList<Transaction> transactionsList = new ArrayList<>();

		public static void main(String[] args) throws IOException {    //declaring function
			printPreamble();
//			itemsList = readItemsFromFile();
//			transactionsList = readTransactionsFromFile();
			itemsList = readItemsFromDb();
			transactionsList = readTransactionsFromFile();
			int userInput = input.nextInt(); //defining int value from userInput
			input.nextLine();
			switch (userInput) {
			case 1:
				Transaction transaction = addItem(itemsList);
				addTransaction(transaction, transactionsList);
				writeToFiles(itemsList, transactionsList);
				break;
			case 2:
//				itemsList = readItemsFromFile();
//				transactionsList = readTransactionsFromFile();
				transaction = editQuantity(itemsList);
				addTransaction(transaction, transactionsList);
				writeToFiles(itemsList, transactionsList);
				break;
			case 3:
//				itemsList = readItemsFromFile();
//				transactionsList = readTransactionsFromFile();
				transaction = removeItem(itemsList);
				addTransaction(transaction, transactionsList);
				writeToFiles(itemsList, transactionsList);
				break;
			case 4:
//				transactionsList = readTransactionsFromFile();
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
		main(args);
	}

		private ArrayList<Item> readItemsFromDb() {
			ArrayList<Item> items = new ArrayList<>((Collection) itemRepository.findAll());
			return items;
		}

		private static void viewTransactionReport(ArrayList<Transaction> transactionsList) {
			int itemCounter = 0;
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\transactions.txt"));
				String transactionsReport;
				while((transactionsReport = bufferedReader.readLine()) != null) {
					System.out.println(transactionsReport);
				}
				bufferedReader.close();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
//		System.out.println("id,description,qtySold,Amount(£),remainingStock,transactionType,day");
//		transactionsList.forEach(transaction -> {
//			System.out.println(transaction.getId() + "," + transaction.getDescription() + "," + transaction.getQtySold() + "," + transaction.getAmount() + "," +
//					transaction.getRemainingStock() + "," + transaction.getTransactionType() + "," + transaction.getTimestamp());
//		});
	}

	private static Transaction removeItem(ArrayList<Item> itemsList) {
		System.out.println("Enter the id of the item you wish to remove.");
		System.out.println("Id, description");
		int id;
		try {
			id = input.nextInt();
			input.nextLine();
			Item itemToDelete = itemsList.stream().filter(item -> item.getId() == id).collect(Collectors.toList()).get(0);
			Transaction transaction = new Transaction(itemToDelete.getId(), itemToDelete.getDescription(), itemToDelete.getQtyStock(),
					itemToDelete.getTotalPrice(), 0, "Total sale of stock", Timestamp.from(Instant.now()));
			return transaction;
		} catch (Exception e) {
			System.err.printf("Exception thrown: %s", e.getMessage());
			System.out.println("Returning to menu.");
		}
		return null;
	}

	private static Transaction editQuantity(ArrayList<Item> itemsList) {
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

	private static void addTransaction(Transaction transaction, ArrayList<Transaction> transactionsList) {
		transactionsList.add(transaction);
	}

	private static void writeToFiles(ArrayList<Item> itemsList, ArrayList<Transaction> transactionsList) throws IOException {
		String itemFile = "C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\items.txt";
		String transactionFile = "C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\transactions.txt";
		FileWriter itemWriter = new FileWriter(itemFile, false);
		itemWriter.write("id,description,unitPrice,qtyStock,totalPrice");
		itemsList.forEach(item -> {
			try {
				itemWriter.write(item.getId() + "," + item.getDescription() + "," + item.getUnitPrice() + "," + item.getQtyStock() + "," + item.getTotalPrice() + ",");
			} catch (IOException e) {
				System.err.printf("IO Exception thrown %s", e.getMessage());
				exit(1);
			}
		});
		itemWriter.close();

		FileWriter transactionsWriter = new FileWriter(transactionFile, false);
		transactionsWriter.write("id,description,qtySold,Amount(£),remainingStock,transactionType,day,");
		transactionsList.forEach(transaction -> {
			try {
				transactionsWriter.write(transaction.getId() + "," + transaction.getDescription() + "," + transaction.getQtySold() + "," + transaction.getAmount() + "," + transaction.getRemainingStock() + "," + transaction.getTransactionType() + "," + transaction.getTimestamp() + ",");
			} catch (IOException e) {
				System.err.printf("IO Exception thrown %s", e.getMessage());
				exit(1);
			}
		});
		transactionsWriter.close();
	}

	private static Transaction addItem(ArrayList<Item> itemsList) {
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
		int id = itemsList.get(itemsList.size()-1).getId() + 1;
		System.out.println(id);
		Item item = new Item(id, description, unitPrice, qtyStock, totalPrice);
		itemsList.add(item);

		Transaction transaction = new Transaction(id, description, qtyStock, totalPrice, qtyStock, "Item added", Timestamp.from(Instant.now()));
		return transaction;
	}

	private static Item readStringToItemObject(String itemFile) {
		String[] itemComponents = StringUtils.commaDelimitedListToStringArray(itemFile);
		Item item = new Item(
				Integer.parseInt(itemComponents[0]),
				itemComponents[1],
				Double.parseDouble(itemComponents[2]),
				Integer.valueOf(itemComponents[3]),
				Double.parseDouble(itemComponents[4]));
		return item;
	}

	private static ArrayList<Item> readItemsFromFile() {
//		String[] itemsFileLines = new String[0];
//		try {
//			itemsFileLines = StringUtils.split(FileUtils.readFileToString(new File("C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\items.txt"), Charset.defaultCharset()), "\n");
//		} catch (IOException e) {
//			System.err.println("Items.txt not found.");
//			exit(1);
//		}
//		ArrayList<String> itemFiles = new ArrayList<>(Arrays.asList(itemsFileLines));
//		if (itemFiles.size() != 0) {
//			itemFiles.remove(0);
//		}
//		ArrayList<Item> items = new ArrayList<>();
//		itemFiles.forEach(itemFile -> {
//			Item item = readStringToItemObject(itemFile);
//			items.add(item);
//		});

		String[] items = new String[99999];
		int itemCounter = 0;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\items.txt"));
			String itemString;
			while((itemString = bufferedReader.readLine()) != null) {
				System.out.println("count: " + itemCounter + 1 + " " + itemString);
				items[itemCounter] = itemString;
				++itemCounter;
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		for (String item : items
			 ) {
			Item itemToAdd = readStringToItemObject(item);
			itemsList.add(itemToAdd);
		}
		return itemsList;
	}

	private static Transaction readStringToTransactionObject(String transactionFile) {
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


	private static ArrayList<Transaction> readTransactionsFromFile() {
		String[] itemsFileLines = new String[0];
		try {
			itemsFileLines = StringUtils.split(FileUtils.readFileToString(new File("C:\\Users\\liamr\\Documents\\GitHub\\inventory-management\\inventory-management\\src\\main\\resources\\transactions.txt"), Charset.defaultCharset()), "\n");
		} catch (IOException e) {
			System.err.println("Transactions.txt not found.");
			exit(1);
		}
		ArrayList<String> itemFiles = new ArrayList<>(Arrays.asList(itemsFileLines));
		if (itemFiles.size() != 0) {
			itemFiles.remove(0);
		}
		ArrayList<Transaction> items = new ArrayList<>();
		itemFiles.forEach(itemFile -> {
			Transaction item = readStringToTransactionObject(itemFile);
			items.add(item);
		});
		return items;
	}

	private static void printPreamble() {
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
}
