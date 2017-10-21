package com.navarone;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Crawler
 * 
 * @author Craig
 * 
 */
public class WebCrawler2 {

	Hashtable<String, Boolean> uniq; // List of visited links

	/**
	 * Constructor
	 */
	WebCrawler2() {
		uniq = new Hashtable<String, Boolean>();
	}

	/**
	 * Read a page given a URL
	 * 
	 * @param url
	 * @return
	 */
	public StringBuilder readPage(URL url) {
		StringBuilder builder = null;
		try {
			URLConnection urlc = url.openConnection();
			BufferedInputStream buffer = new BufferedInputStream(urlc.getInputStream());
			builder = new StringBuilder();
			int byteRead;
			while ((byteRead = buffer.read()) != -1)
				builder.append((char) byteRead);
			buffer.close();
			return builder;

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return builder;
	}

	/**
	 * Parse text for links
	 * 
	 * @param builder
	 * @return
	 */
	private List<String> parse(StringBuilder builder) {
		Scanner s = null;
		try {
			List<String> links = new ArrayList<String>();
			if (builder == null)
				return null;
			String pattern = "(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?";
			s = new Scanner(builder.toString());
			s.useDelimiter("\n");
			while (s.hasNext()) {
				String string = s.next();
				Matcher m2 = Pattern.compile(pattern).matcher(string);
				while (m2.find()) {
					String url = m2.group();
					if (url.startsWith("href") && !url.endsWith(".pdf") && !url.endsWith(".doc")) {
						url = url.substring(6);
						System.out.println(url);
						links.add(url);
					}
				}
			}
			return links;
		} finally {
			if (s != null) {
				s.close();
			}
		}
	}

	/**
	 * Starts the program
	 *
	 * @param args
	 *            the command line arguments String seed : the seed URL int size :
	 *            the maximum size boolean bfs : whether to do breadth-first
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			printUsage();
			return;
		}
		String seed = null;
		int maxSize = 10;
		boolean bfs = true;
		try {
			seed = args[0];
			maxSize = Integer.parseInt(args[1]);
			bfs = args[2].equalsIgnoreCase("bfs") ? true : false;
		} catch (Exception x) {
			printUsage();
			return;
		}

		PriorityTree tree = new PriorityTree(maxSize, seed);
		WebCrawler2 W = new WebCrawler2();
		W.crawl(tree, bfs);

	}

	/**
	 * Crawls once only to populate the Priority Tree
	 * 
	 * @param tree
	 * @param bfs
	 */
	@Deprecated
	public void crawlFirst(PriorityTree tree, boolean bfs) {
		URL u = null;
		try {
			u = new URL(tree.getSeed().url);
			System.out.println("Crawling " + u.toString());
			uniq.put(u.toString(), true);
			List<String> links = parse(readPage(u));
			for (String link : links)
				tree.put(u.toString(), link);
		} catch (Exception x) {
		}
	}

	/**
	 * Crawling function
	 * 
	 * @param tree
	 * @param bfs
	 */
	private void crawl(PriorityTree tree, boolean bfs) {
		URL u = null;
		while (!tree.isFull()) {
			try {
				if (tree.getSize() == 1)
					u = new URL(tree.getSeed().url);
				else
					u = new URL(tree.get(bfs));
				System.out.println("Crawling " + u.toString());
				if (uniq.containsKey(u.toString()))
					continue;
				uniq.put(u.toString(), true);
				List<String> links = parse(readPage(u));
				if (links != null)
					for (String link : links)
						tree.put(u.toString(), link);
			} catch (Exception x) {
				continue;
			}
		}
		System.out.println("Tree size is " + tree.getSize());
		System.out.println("Tree full");
	}

	/**
	 * Prints usage to STDOUT
	 */
	private static void printUsage() {
		System.out.println("Usage WebCrawler2 seedURL maxURLsize [bfs|dfs]");
	}
}