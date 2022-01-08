package com.nobot.plugin.image.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.image.entity.TagEntity;

import java.util.List;

@Dao
public interface TagDAO extends YuDao<TagEntity,Long>
{
	List<TagEntity> findByName(String name);
}
