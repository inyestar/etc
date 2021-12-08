package com.inyestar.etc.dinner;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;


public class Main {
	
	private static String HISTORY = "history";
	private static String LASTDATE = ".lastdate";
	private static String STORE = ".store";
	private static String DATEFORMAT = "yyyy-MM-dd";
	
	/**
	 * .lastdate ���Ͽ� ����Ǿ� �ִ� ������ ���� �ð��� �����´�.
	 * 
	 * @param today
	 * @return
	 * @throws IOException
	 */
	private static LocalDate getLastDate(LocalDate today) throws IOException {
		Path loc = Paths.get(HISTORY, LASTDATE);
		if(!Files.exists(loc)) {
			return today;
		}
		
		List<String> content = Files.readAllLines(loc);
		if(content.isEmpty()) {
			return today;
		}
		
		return LocalDate.parse(content.get(0), DateTimeFormatter.ofPattern(DATEFORMAT));
	}
	
	/**
	 * .store ���Ͽ� ����Ǿ� �ִ� ���� ����� ��
	 * @return
	 * @throws IOException
	 */
	private static List<String> getLastMenu() throws IOException {
		Path loc = Paths.get(HISTORY, STORE);
		
		if(!Files.exists(loc)) {
			return new ArrayList<>();
		}
		
		return Files.readAllLines(loc, StandardCharsets.UTF_8);
	}
	
	/**
	 * .store ���Ͽ� ��� ���� �޴� ����
	 * @param menu
	 * @throws IOException
	 */
	private static void storeLastMenu(String menu) throws IOException {
		Path loc = Paths.get(HISTORY, STORE);
		
		if(!Files.exists(loc)) {
			Files.write(loc, "".getBytes());
		}
		
		menu += "\n";
		Files.write(loc, menu.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
		
	}

	public static void main(String[] args) {

		// arguments�� ���� ��� null�� �ƴѰ� Ȯ��
		if(args.length == 0) {
			System.err.println("File name not found");
			System.exit(1);
		}
		
		String fileName = args[0];
		if(!Files.exists(Paths.get(fileName))) {
			System.err.printf("File [%s] not found%n", fileName);
			System.exit(1);
		}
		
		// history ���丮 ����
		if(!Files.exists(Paths.get(HISTORY))) {
			try {
				Files.createDirectory(Paths.get(HISTORY));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try{
			
			// ������ ���� ��¥ ���ϱ�
			LocalDate today = LocalDate.now();
			LocalDate lastday = getLastDate(today);
			long range = Duration.between(today.atTime(0, 0), lastday.atTime(0, 0)).toDays();
			
			if(range < 0) {
				// crear .store
				Files.write(Paths.get(HISTORY, STORE), "".getBytes(StandardCharsets.UTF_8));
			}
			
			List<String> candidate = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
			
			if(candidate.isEmpty()) {
				throw new IOException("No menu found");
			}
			
			// ������ ���°� �ֵ�
			List<String> trash = getLastMenu();
			candidate.removeAll(trash);
			
			
			String result = candidate.get(new Random().nextInt(candidate.size()));
			System.out.println(result);
			
			// ���°� ����
			storeLastMenu(result);
			
			// ���� ��¥ ����
			Files.write(Paths.get(HISTORY, LASTDATE), today.format(DateTimeFormatter.ofPattern(DATEFORMAT)).getBytes(StandardCharsets.UTF_8));
		} catch(UncheckedIOException | IOException ie) {
			ie.printStackTrace();
		} 
	}

}
