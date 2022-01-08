package com.nobot.plugin.image.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.image.entity.OperationRecord;

@Dao
public interface RecorderDAO extends YuDao<OperationRecord,Long>
{
}
