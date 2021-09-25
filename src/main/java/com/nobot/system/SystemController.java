package com.nobot.system;

import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Global;
import com.IceCreamQAQ.Yu.as.ApplicationService;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.nobot.system.annotation.CreateFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

@PrivateController
@GroupController
@CreateFile("OP.properties")
public class SystemController implements ApplicationService
{
	long SOPNum=-1;
	public long setSOPNum(Properties properties)
	{

		JDialog jDialog=new JDialog();
		jDialog.setTitle("设置超级管理员");
		jDialog.setSize(500,100);
		jDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		TextField textField=new TextField();
		textField.setSize(380,80);
		textField.setFont(new Font("宋体",Font.BOLD,50));
		jDialog.add(textField);
		Button button=new Button();
		button.setLabel("确定");
		button.setSize(80,80);
		button.addActionListener(e -> {
			try
			{
				SOPNum= Long.parseLong(textField.getText());
				properties.setProperty("SOP",Long.toString(SOPNum));
				JOptionPane.showMessageDialog(jDialog,"将"+SOPNum+"设置成超级管理员\r\n后续可以在OP.properties中修改"
						,null,JOptionPane.INFORMATION_MESSAGE);
				jDialog.setVisible(false);
			}
			catch (NumberFormatException numberFormatException)
			{
				JOptionPane.showMessageDialog(jDialog,"输入有误","错误",JOptionPane.ERROR_MESSAGE);
			}
		});
		jDialog.add(button);
		jDialog.setVisible(true);
		while (SOPNum==-1)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return SOPNum;
	}

	@Global
	@Before
	public void sendOPNum(BotActionContext actionContext)
	{

	}

	@Override
	public void init()
	{
		File file=new File("OP.properties");
		Properties properties=new Properties();
		try
		{
			InputStreamReader inputStreamReader=new InputStreamReader(new FileInputStream(file),"UTF-8");
			properties.load(inputStreamReader);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			file.delete();
			try
			{
				file.createNewFile();
				InputStreamReader inputStreamReader=new InputStreamReader(new FileInputStream(file),"UTF-8");
				properties.load(inputStreamReader);
			}
			catch (IOException ioException)
			{
				ioException.printStackTrace();
				return;
			}
			setSOPNum(properties);
			return;
		}
		try
		{
			SOPNum=Long.parseLong(properties.getProperty("SOP"));
		}
		catch (NumberFormatException e)
		{
			setSOPNum(properties);
		}
	}

	@Override
	public void start()
	{

	}

	@Override
	public void stop()
	{

	}

	@Override
	public int width()
	{
		return 0;
	}
}
