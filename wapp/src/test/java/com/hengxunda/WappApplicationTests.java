package com.hengxunda;

import com.hengxunda.dao.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WappApplicationTests {

	@Autowired
	private UserMapper userMapper;

	@Test
	public void t1() {

		System.out.println(userMapper.selectByPrimaryKey("123").getName());
	}

}
