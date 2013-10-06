package edu.osu.cse._2221.project5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.xmltree.XMLTree;
import components.xmltree.XMLTree1;

public class RSSFeed {

	private String totalHtml = "";
	private String url, name, fileName;

	public RSSFeed(String url, String name, String fileName) {
		System.out.println("Processing:");
		System.out.println(url);
		System.out.println(name);
		System.out.println(fileName);
		this.url = url;
		this.name = name;
		this.fileName = fileName;
		this.process();
	}

	public String getUrl() {
		return this.url;
	}

	public String getTitle() {
		return this.name;
	}

	public String getFileName() {
		return this.fileName;
	}

	private void addLine(String line) {
		this.totalHtml += line + "\n";
	}

	/**
	 * Outputs the "opening" tags in the generated HTML file. These are the
	 * expected elements generated by this method:
	 * 
	 * <html>
	 * <head>
	 * <title>the channel tag title as the page title</title>
	 * </head>
	 * <body>
	 *  <h1>the page title inside a link to the <channel> link<h1>
	 *  <p>the channel description</p>
	 *  <table>
	 *   <tr>
	 *    <th>Date</th>
	 *    <th>Source</th>
	 *    <th>News</th>
	 *   </tr>
	 * 
	 * @param channel
	 *            the channel element XMLTree
	 * @param out
	 *            the output stream
	 * @updates {@code out.content}
	 * @requires <pre>
	 * {@code [the root of channel is a <channel> tag] and out.is_open}
	 * </pre>
	 * @ensures <pre>
	 * {@code out.content = #out.content * [the HTML "opening" tags]}
	 * </pre>
	 */
	private void outputHeader(XMLTree channel, SimpleWriter out) {
		assert channel != null : "Violation of: channel is not null";
		assert out != null : "Violation of: out is not null";
		assert channel.isTag() && channel.label().equals("channel") : ""
		+ "Violation of: the label root of channel is a <channel> tag";
		assert out.isOpen() : "Violation of: out.is_open";

		addLine("<html>");

		int titleIndex = getChildElement(channel, "title");
		int linkIndex = getChildElement(channel, "link");
		int descriptionIndex = getChildElement(channel, "description");
		addLine("<head>");
		addLine("<title>");
		addLine(channel.child(titleIndex).child(0).label());
		addLine("</title>");
		addLine("</head>");
		addLine("<body>");
		addLine("<h1><a href=\"" + channel.child(linkIndex).child(0).label() + "\">" + channel.child(titleIndex).child(0).label() +"</a></h1>");
		addLine("<p>" + channel.child(descriptionIndex).child(0).label() +"</p>");
		addLine("<table border=\"1\">");
		addLine("<tr>");
		addLine("<th>Date</th>");
		addLine("<th>Source</th>");
		addLine("<th>News</th>");
		addLine("</tr>");
	}

	/**
	 * Outputs the "closing" tags in the generated HTML file.  These are the
	 * expected elements generated by this method:
	 * 
	 *  </table>
	 * </body>
	 * </html>
	 * 
	 * @param out
	 *            the output stream
	 * @updates {@code out.contents}
	 * @requires <pre>
	 * {@code out.is_open}
	 * </pre>
	 * @ensures <pre>
	 * {@code out.content = #out.content * [the HTML "closing" tags]}
	 * </pre>
	 */
	private void outputFooter(SimpleWriter out) {
		assert out != null : "Violation of: out is not null";
		assert out.isOpen() : "Violation of: out.is_open";

		addLine("</table>");
		addLine("</body>");
		addLine("</html>");
	}

	/**
	 * Finds the first occurrence of the given tag among the children of the
	 * given {@code XMLTree} and return its index; returns -1 if not found.
	 * 
	 * @param xml
	 *            the {@code XMLTree} to search
	 * @param tag
	 *            the tag to look for
	 * @return the index of the first child of type tag of the {@code XMLTree}
	 *         or -1 if not found
	 * @requires <pre>
	 * {@code [the label of the root of xml is a tag]}
	 * </pre>
	 * @ensures <pre>
	 * {@code getChildElement =
	 *  [the index of the first child of type tag of the {@code XMLTree} or
	 *   -1 if not found]}
	 * </pre>
	 */
	private int getChildElement(XMLTree xml, String tag) {
		assert xml != null : "Violation of: xml is not null";
		assert tag != null : "Violation of: tag is not null";
		assert xml.isTag() : "Violation of: the label root of xml is a tag";

		for (int i = 0; i < xml.numberOfChildren(); i++) {
			if (xml.child(i).label().equals(tag)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Processes one news item and outputs one table row. The row contains three
	 * elements: the publication date, the source, and the title (or
	 * description) of the item.
	 * 
	 * @param item
	 *            the news item
	 * @param out
	 *            the output stream
	 * @updates {@code out.content}
	 * @requires <pre>
	 * {@code [the label of the root of item is an <item> tag] and out.is_open}
	 * </pre>
	 * @ensures <pre>
	 * {@code out.content = #out.content *
	 *   [an HTML table row with publication date, source, and title of news item]}
	 * </pre>
	 */
	private void processItem(XMLTree item, SimpleWriter out) {
		assert item != null : "Violation of: item is not null";
		assert out != null : "Violation of: out is not null";
		assert item.isTag() && item.label().equals("item") : ""
		+ "Violation of: the label root of item is an <item> tag";
		assert out.isOpen() : "Violation of: out.is_open";

		String title = null;
		String description = "No description available";
		String newsLink = null;
		String pubDate = "No publication date available";
		String source = "No source available";
		String sourceLink = null;

		int titleIndex = getChildElement(item, "title");
		int descriptionIndex = getChildElement(item, "description");
		int linkIndex = getChildElement(item, "link");
		int pubDateIndex = getChildElement(item, "pubDate");
		int sourceIndex = getChildElement(item, "source");

		if (titleIndex >= 0) {

			title = item.child(titleIndex).child(0).label();
		}

		if (descriptionIndex >= 0) {
			XMLTree child = item.child(descriptionIndex);
			if (child.numberOfChildren() > 0)
				description = item.child(descriptionIndex).child(0).label();
		}

		if (linkIndex >= 0)
			newsLink = item.child(linkIndex).child(0).label().replaceAll("\"", "");

		if (pubDateIndex >= 0)
			pubDate = item.child(pubDateIndex).child(0).label();

		if (sourceIndex >= 0) {
			source = item.child(sourceIndex).child(0).label();
			sourceLink = item.child(sourceIndex).attributeValue("url");
		}

		addLine("<tr>");
		addLine("<td>" + pubDate + "</td>");

		if (sourceLink != null)
			addLine("<td>" + "<a href=\"" + sourceLink + "\">" + source + "</a>" + "</td>");
		else
			addLine("<td>" + source + "</td>");

		if (newsLink != null)
			addLine("<td>" + "<a href=\"" + newsLink + "\">" + (title == null ? (description == null ? "No title available" : description) : title) + "</a>" + "</td>");
		else
			addLine("<td>" + source + "</td>");

		addLine("</tr>");
	}

	/**
	 * Processes an RSS feed into an html file that can be displayed in a web browser.
	 */
	public void process() {
		SimpleWriter out = new SimpleWriter1L();

		XMLTree tree = new XMLTree1(this.url);
		if (tree.isTag() && tree.label().equals("rss") 
				&& tree.hasAttribute("version") && tree.attributeValue("version").equals("2.0")) {
			XMLTree channel = tree.child(0);
			outputHeader(channel, out);
			for (int i = 0; i < channel.numberOfChildren(); i++) {
				XMLTree child = channel.child(i);
				if (child.isTag() && child.label().equals("item")) {
					XMLTree item = channel.child(i);
					processItem(item, out);
				}
			}
			outputFooter(out);

		}
		out.close();
	}

	private void writeToFile() {
		File feedHtml = new File("./" + this.fileName);
		try (FileOutputStream fOut = new FileOutputStream(feedHtml)) {

			byte[] contents = this.totalHtml.getBytes();
			fOut.write(contents);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
