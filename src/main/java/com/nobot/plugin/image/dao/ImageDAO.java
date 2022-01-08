package com.nobot.plugin.image.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.image.entity.ImageEntity;

@Dao
public interface ImageDAO extends YuDao<ImageEntity,Long>
{
	ImageEntity findByName(String name);
}
