import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.print.Doc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.cleverleo.common.C;
import net.cleverleo.common.Model;

public class Ebook {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		try {
			Ebook.getBookList();
			// Ebook.getContentHandle("http://www.duokan.com/%E7%99%BD%E9%97%A8%E6%9F%B3%EF%BC%88%E5%85%A8%E4%B8%89%E5%86%8C%EF%BC%89/b/40342");
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public static void getBookList() throws SQLException, ParseException,
			IOException {
		C con = new C(
				"http://www.duokan.com/%E5%85%A8%E9%83%A8%E5%9B%BE%E4%B9%A6/c/1-1");
		Document doc = con.get();
		Integer maxPage = Integer.valueOf(doc.select("#bookpage-a li").get(4)
				.text());

		HashSet<String> linkSet = new HashSet<String>();

		for (int i = 1; i <= maxPage; i++) {
			con.setUrl("http://www.duokan.com/%E5%85%A8%E9%83%A8%E5%9B%BE%E4%B9%A6/c/1-"
					+ i);
			doc = con.get();

			Elements items = doc.select(".j-list>li");

			for (int n = 0; n < items.size(); n++) {
				Element item = items.get(n);

				String link = "http://www.duokan.com"
						+ item.select(".title").attr("href");
				linkSet.add(link);

			}
		}
		System.out.println(linkSet.size());
		Ebook.getContent(linkSet);
	}

	public static void getContent(Set<String> linkSet) throws IOException,
			SQLException, ParseException {
		Iterator<String> it = linkSet.iterator();
		while (it.hasNext()) {
			String link = it.next();

			Ebook.getContentHandle(link);
		}
	}

	public static void getContentHandle(String link) throws IOException,
			SQLException, ParseException {
		C con = new C(link);
		Document doc = con.get();
		HashMap<String, String> data = new HashMap<String, String>();

		String title = doc.select(".desc h1").text();
		String content = doc.select("article.intro").html();

		System.out.println(title);

		data.put("title", title);
		data.put("description", content);

		Element price_dom = doc.select(".price").get(0);
		String price_now = price_dom.select("em").text();
		if (!price_now.equals("免费")) {
			price_now = price_now.split(" ")[1];
		} else {
			price_now = "0";
		}

		data.put("price_now", price_now);
		Elements price_list = price_dom.select("i");
		for (int i = 0; i < price_list.size(); i++) {
			if (price_list.get(i).textNodes().get(0).toString().equals("原价")) {
				data.put("price_org", price_list.get(i).select("del").text()
						.split(" ")[1]);
				continue;
			}

			if (price_list.get(i).textNodes().get(0).toString().equals("纸书")) {
				data.put("price_book", price_list.get(i).select("del").text()
						.split(" ")[1]);
				continue;
			}
		}

		data.put("author", doc.select(".author").get(0).text());
		Model book_type = new Model("think_book_type");

		ResultSet btResult = book_type.where("name",
				doc.select("#dkclassify").text()).find();
		String book_type_id = null;
		if (btResult == null) {
			book_type_id = "0";
		} else {
			book_type_id = btResult.getString("id");
		}

		data.put("book_type_id", book_type_id);
		data.put("img", doc.select(".cover img").get(0).attr("src"));

		Elements info_dom = doc.select("article.data li");

		for (int i = 0; i < info_dom.size(); i++) {
			if (info_dom.get(i).textNodes().get(0).toString().equals("书号：")) {
				data.put("isbn", info_dom.get(i).select("span").text()
						.replaceAll("-", ""));
				continue;
			}

			if (info_dom.get(i).textNodes().get(0).toString().equals("译者：")) {
				data.put("translators", info_dom.get(i).select("span").text());
				continue;
			}

			if (info_dom.get(i).textNodes().get(0).toString().equals("出版：")) {
				long pubdate = (new SimpleDateFormat("yyyy-mm-dd")).parse(
						info_dom.get(i).select("span").text()).getTime() / 1000;
				if (pubdate > (new Date().getTime() / 1000)) {
					pubdate = 0;
				}
				data.put("pubdate", String.valueOf(pubdate));
				continue;
			}
		}

		Iterator<String> it = data.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			System.out.println(String.format("%s=%s", key, data.get(key)));
		}

		data.put("platform_id", "1");

		Model ebook = new Model("think_book");
		ebook.add(data);
	}

	public static void getType() {
		try {
			C con = new C(
					"http://www.duokan.com/%E5%85%A8%E9%83%A8%E5%9B%BE%E4%B9%A6/c/1-1");
			Document doc = con.get();
			Elements items = doc.select(".level1 span");
			Model book_type = new Model("think_book_type") {
			};
			for (int i = 0; i < items.size(); i++) {
				Element item = items.get(i);
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("name", item.text());
				book_type.add(data);
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

}
