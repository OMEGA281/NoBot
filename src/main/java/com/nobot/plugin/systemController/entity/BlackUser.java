package com.nobot.plugin.systemController.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Table
@Data
@Entity
public class BlackUser
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long ID;

	@Column(nullable = false)
	public long userNum;

	@Column(nullable = false)
	public long groupNum;

	/**
	 * 根据位计算
	 */
	@Column
	public int otherBooleanSetting=0;

	@CreationTimestamp
	public Timestamp creatTime;

	@Column
	public String info;
}
