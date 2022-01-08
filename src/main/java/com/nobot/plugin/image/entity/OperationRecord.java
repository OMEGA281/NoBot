package com.nobot.plugin.image.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Table
@Entity
public class OperationRecord
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private long operator;

	@Column(nullable = false)
	private String imageName;

	@Column(nullable = false)
	private String tagName;

	@Column(nullable = false)
	private boolean isAdd;

	@CreationTimestamp
	private Timestamp timestamp;
}
