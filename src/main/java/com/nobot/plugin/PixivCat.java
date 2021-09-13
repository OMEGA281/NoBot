package com.nobot.plugin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Catch;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.IceCreamQAQ.Yu.util.Web;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.icecreamqaq.yuq.message.MessageLineQ;
import com.nobot.system.annotation.CreateDir;
import com.nobot.tool.FileUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@GroupController
@PrivateController
@CreateDir("PixivCatTmp")
public class PixivCat
{
	public static final Pattern tooMany=Pattern.compile("這個作品ID中有只有 (\\d+) 張圖片");
	public static final Pattern doubleImage=Pattern.compile("這個作品ID中有 (\\d+) 張圖片");
	public static final Pattern nullImage=Pattern.compile("這個作品可能已被刪除，或無法取得。");

	private static final String station="http://pixiv.cat/";

	private int maxLength=3500;
	@Inject
	MessageItemFactory factory;

	@Inject
	FileUtils fileUtils;

	@Inject
	Web web;

	@Action("pixiv {num}")
	public Message test(String num, Group group, Contact qq) throws IOException
	{
		if(group!=null)
			group.sendMessage(new Message().plus("查询中 请稍后"));
		else
			qq.sendMessage(new Message().plus("查询中 请稍后"));
		String url=station+num+".jpg";
		OkHttpClient okHttpClient=new OkHttpClient();
		Request request=new Request.Builder().url(url).get().build();
		Call call=okHttpClient.newCall(request);
		Response response=call.execute();

		InputStream inputStream=response.body().byteStream();
		BufferedImage image=ImageIO.read(inputStream);

		if(image==null)
		{
			String s=response.body().string();
			Matcher matcher= tooMany.matcher(s);
			if(matcher.find())
				return new Message().plus("本作品ID有"+matcher.group(1)+"张，请在序号后加\"-序号\"来查看");
			matcher=doubleImage.matcher(s);
			if(matcher.find())
				return new Message().plus("本作品ID有"+matcher.group(1)+"张，请在序号后加\"-序号\"来查看");
			matcher=nullImage.matcher(s);
			if(matcher.find())
				return new Message().plus("你所寻找的作品不存在");
			return new Message().plus("未知错误");
		}

		File tmpFile=new File("PixivCatTmp\\"+num+".jpg");
		fileUtils.write(tmpFile,inputStream);
		MessageLineQ messageLineQ=new Message().lineQ();
		if(group!=null)
			messageLineQ.at(qq.getId());
		messageLineQ.imageByFile(tmpFile).text(url);
		if (group!=null)
			group.sendMessage(messageLineQ);
		else
			qq.sendMessage(messageLineQ);
		tmpFile.delete();
		return null;
	}

	@Catch(error = IOException.class)
	public String work()
	{
		return "发生错误";
	}
}
