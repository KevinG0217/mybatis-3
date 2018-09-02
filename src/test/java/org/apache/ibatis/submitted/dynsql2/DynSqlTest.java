/**
 * Copyright 2009-2018 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.submitted.dynsql2;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DynSqlTest {

    protected static SqlSessionFactory sqlSessionFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/dynsql2/MapperConfig.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }

        BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
                "org/apache/ibatis/submitted/dynsql2/CreateDB.sql");
    }

    @Test
    public void testDynamicSelectWithTypeHandler() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            List<Name> names = new ArrayList<Name>();

            Name name = new Name();
            name.setFirstName("Fred");
            name.setLastName("Flintstone");
            names.add(name);

            name = new Name();
            name.setFirstName("Barney");
            name.setLastName("Rubble");
            names.add(name);

            Parameter parameter = new Parameter();
            parameter.setNames(names);

            List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql2.dynamicSelectWithTypeHandler", parameter);

            assertTrue(answer.size() == 2);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleSelect() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Map<String, Object> answer = (Map<String, Object>) sqlSession.selectOne("org.apache.ibatis.submitted.dynsql2.simpleSelect", 1);

            assertEquals(answer.get("ID"), 1);
            assertEquals(answer.get("FIRSTNAME"), "Fred");
            assertEquals(answer.get("LASTNAME"), "Flintstone");
        }
    }
}
