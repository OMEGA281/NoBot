package com.nobot.plugin.dice.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 技能和技能编号的映射
 */
@Entity
@Table
@Data
public class COCCardSkillName
{
	public enum defaultSkill
	{
		时代, 年龄, 职业, 力量, 体质, 体型, 敏捷, 外貌, 智力, 意志, 教育, 体力, 理智, 幸运, 魔法, 会计, 表演, 动物驯养, 人类学, 估价, 考古学,
		炮术, 天文学, 斧头, 生物学, 植物学, 弓术, 斗殴, 电锯, 魅惑, 化学, 攀爬, 计算机使用, 信用评级, 密码学, 克苏鲁神话, 爆破, 乔装, 潜水,
		闪避, 汽车驾驶, 电气维修, 电子学, 话术, 美术, 急救, 连枷, 火焰喷射器, 司法科学, 伪造, 绞索, 地质学, 手枪, 重武器, 历史, 催眠, 恐吓,
		跳跃, 母语, 法律, 图书馆使用, 聆听, 锁匠, 机关枪, 数学, 机械维修, 医学, 气象学, 博物学, 领航, 神秘学, 操作重型机械, 说服, 药学,
		摄影, 物理, 精神分析, 心理学, 读唇, 骑术, 步枪, 霰弹枪, 妙手, 矛, 侦察, 前行, 冲锋枪, 生存, 剑, 游泳, 投掷, 追踪, 鞭子, 动物学,
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String skillName;

}
