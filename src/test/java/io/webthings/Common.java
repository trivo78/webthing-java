package io.webthings;

import io.webthings.webthing.JSONEntity;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Lorenzo
 */
public class Common {
    public static class TestFieldId implements Comparable<TestFieldId> {
        public final String jsonName;
        public final String fieldName;
        private final String key;

        public TestFieldId(String j, String f) {
            jsonName = j;
            fieldName = f;
            key = jsonName + "#" + fieldName;
        }

        @Override
        public int compareTo(TestFieldId o) {
            return key.compareTo(o.key);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || (o instanceof TestFieldId) == false) {
                return false;
            }

            final TestFieldId oi = (TestFieldId)o;
            return key.equals(oi.key);
        }
    }

    private static void checkSettedField(Object instance,
                                         String name,
                                         Object value) {
        final String FN_NAME =
                MethodHandles.lookup().lookupClass() + ".checkSettedField(()";

        try {
            final Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            assertEquals("Fields didn't match", field.get(instance), value);
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static void setGettedField(Object instance,
                                      String name,
                                      Object value) {
        final String FN_NAME =
                MethodHandles.lookup().lookupClass() + ".setGettedField()";

        try {
            final Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static <B, T> void checkGetter(Class c,
                                              String methodBase,
                                              String variable,
                                              T value) {
        final String FN_NAME =
                MethodHandles.lookup().lookupClass() + ".checkGetter()";
        try {
            final B pojo = (B)c.newInstance();
            final Field field = c.getDeclaredField(variable);
            field.setAccessible(true);
            field.set(pojo, value);

            final Method m = c.getDeclaredMethod("get" + methodBase);
            final T ret = (T)m.invoke(pojo);

            assertEquals(ret, value);
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static <B, T, C extends Collection<T>> void checkGetterOnCollection(
            Class c,
            String methodBase,
            String variable,
            T value,
            Class coll) {
        final String FN_NAME = MethodHandles.lookup().lookupClass() +
                ".checkGetterOnCollection()";
        try {
            final B pojo = (B)c.newInstance();
            final Field field = c.getDeclaredField(variable);
            field.setAccessible(true);
            C realValue = (C)coll.newInstance();
            realValue.add(value);

            field.set(pojo, realValue);

            final Method m = c.getDeclaredMethod("get" + methodBase);
            final T ret = (T)m.invoke(pojo);

            assertEquals(ret, value);
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static <B, T> void checkGetterOnMap(Class c,
                                                   String methodBase,
                                                   String variable,
                                                   String key,
                                                   T value) {
        final String FN_NAME = MethodHandles.lookup().lookupClass() +
                ".checkGetterOnCollection()";
        try {
            final B pojo = (B)c.newInstance();
            final Field field = c.getDeclaredField(variable);
            field.setAccessible(true);
            Map<String, T> realValue = new TreeMap<>();
            realValue.put(key, value);

            field.set(pojo, realValue);

            final Method m =
                    c.getDeclaredMethod("get" + methodBase, String.class);
            final T ret = (T)m.invoke(pojo, key);

            assertEquals(ret, value);
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static <B, T, C extends Collection<T>> void checkSetterOnCollection(
            Class c,
            String methodBase,
            String variable,
            T value,
            Class coll) {
        final String FN_NAME =
                MethodHandles.lookup().lookupClass() + ".checkSetter()";
        try {
            final B pojo = (B)c.newInstance();
            final Method m =
                    c.getDeclaredMethod("set" + methodBase, value.getClass());

            m.invoke(pojo, value);

            final Field field = c.getDeclaredField(variable);
            field.setAccessible(true);

            final C realRes = (C)field.get(pojo);

            final T res = (T)(realRes != null && realRes.size() > 0 ?
                                  realRes.toArray()[0] :
                                  null);

            assertEquals(value, res);
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static <U, T> void checkSetter(Class c,
                                          String methodBase,
                                          Class methodParam,
                                          String variable,
                                          T value) {
        final String FN_NAME =
                MethodHandles.lookup().lookupClass() + ".checkSetter()";
        try {
            final U pojo = (U)c.newInstance();

            final Method m =
                    c.getDeclaredMethod("set" + methodBase, methodParam);

            m.invoke(pojo, value);

            final Field field = c.getDeclaredField(variable);
            field.setAccessible(true);
            final T res = (T)field.get(pojo);

            assertEquals(value, res);
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static <B, T> void checkSetter(Class c,
                                              String methodBase,
                                              String variable,
                                              T value) {
        checkSetter(c, methodBase, value.getClass(), variable, value);
    }

    public static <B, T> void checkAddToCollection(Class c,
                                                       String methodName,
                                                       String variable,
                                                       T value) {
        final String FN_NAME =
                MethodHandles.lookup().lookupClass() + ".checkGetter()";

        try {

            final B pojo = (B)c.newInstance();


            final Field field = c.getDeclaredField(variable);
            field.setAccessible(true);

            final Collection<T> res_before = (Collection<T>)field.get(pojo);
            final int count_before =
                    (res_before == null ? 0 : res_before.size());

            final Method m = c.getDeclaredMethod(methodName, value.getClass());
            m.invoke(pojo, value);

            final Collection<T> res_after = (Collection<T>)field.get(pojo);

            final int count_after = (res_after == null ? 0 : res_after.size());

            assertEquals("Collection size not incremented !",
                         count_before,
                         count_after - 1);
            assertTrue("Value not added to collection",
                       res_after != null && res_after.contains(value));
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static <B, T> void checkAddToCollection(Class c,
                                                       String methodName,
                                                       String variable,
                                                       String key,
                                                       T value) {
        final String FN_NAME = MethodHandles.lookup().lookupClass() +
                ".checkAddToCollection()";

        try {

            final B pojo = (B)c.newInstance();


            final Field field = c.getDeclaredField(variable);
            field.setAccessible(true);

            final Map<String, T> res_before =
                    (Map<String, T>)field.get(pojo);
            final int count_before =
                    (res_before == null ? 0 : res_before.size());

            final Method m = c.getDeclaredMethod(methodName,
                                                 String.class,
                                                 value.getClass());
            m.invoke(pojo, key, value);

            final Map<String, T> res_after =
                    (Map<String, T>)field.get(pojo);

            final int count_after = (res_after == null ? 0 : res_after.size());

            assertEquals("Collection size not incremented !",
                         count_before,
                         count_after - 1);
            assertTrue("Value not added to collection",
                       res_after != null && res_after.containsKey(key) &&
                               res_after.get(key).equals(value));
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static <B, T> void checkRemoveFromCollection(Class c,
                                                            String methodName,
                                                            String variable,
                                                            T value) {
        final String FN_NAME = MethodHandles.lookup().lookupClass() +
                ".checkRemoveFromCollection()";

        try {

            final B pojo = (B)c.newInstance();


            final Field field = c.getDeclaredField(variable);
            field.setAccessible(true);

            Collection<T> res_before = (Collection<T>)field.get(pojo);


            if (res_before == null) {
                switch (field.getType().toString()) {
                    case "interface java.util.Set":
                        res_before = new TreeSet<>();
                        break;
                    case "interface java.util.List":
                        res_before = new ArrayList<>();
                        break;
                    default:
                        res_before =
                                (Collection<T>)field.getType().newInstance();
                        break;
                }

                field.set(pojo, res_before);
            }

            res_before.add(value);
            final int count_before =
                    (res_before == null ? 0 : res_before.size());


            final Method m = c.getDeclaredMethod(methodName, value.getClass());
            m.invoke(pojo, value);

            final Collection<T> res_after = (Collection<T>)field.get(pojo);


            final int count_after = (res_after == null ? 0 : res_after.size());

            assertEquals("Collection size not decremented !",
                         count_before,
                         count_after + 1);
            assertFalse("Value not still in collection",
                        res_after != null && res_after.contains(value));
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static <B, T> void checkRemoveFromCollection(Class c,
                                                            String methodName,
                                                            String variable,
                                                            String key,
                                                            T value) {
        final String FN_NAME = MethodHandles.lookup().lookupClass() +
                ".checkRemoveFromCollection()";

        try {

            final B pojo = (B)c.newInstance();


            final Field field = c.getDeclaredField(variable);
            field.setAccessible(true);

            Map<String, T> res_before = (Map<String, T>)field.get(pojo);

            if (res_before == null) {
                res_before = new TreeMap<>();
                field.set(pojo, res_before);
            }

            res_before.put(key, value);
            final int count_before =
                    (res_before == null ? 0 : res_before.size());


            final Method m = c.getDeclaredMethod(methodName, String.class);
            m.invoke(pojo, key);

            final Map<String, T> res_after =
                    (Map<String, T>)field.get(pojo);


            final int count_after = (res_after == null ? 0 : res_after.size());

            assertEquals("Collection size not decremented !",
                         count_before,
                         count_after + 1);
            assertFalse("Value not still in collection",
                        res_after != null && res_after.containsKey(key));
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static void addTestFieldVal(String j,
                                       String f,
                                       Object v,
                                       Map<TestFieldId, Object> tgt) {
        tgt.put(new TestFieldId(j, f), v);
    }

    public static <T extends JSONEntity> void checkToJSON(Class c,
                                                            Map<TestFieldId, Object> vals) {
        final String FN_NAME =
                MethodHandles.lookup().lookupClass() + ".checkToJSON()";

        try {
            final T pojo = (T)c.newInstance();
            //first, set all fields to given value
            for (final Map.Entry<TestFieldId, Object> e : vals.entrySet()) {
                final Field f =
                        pojo.getClass().getDeclaredField(e.getKey().fieldName);
                f.setAccessible(true);
                f.set(pojo, e.getValue());
            }
            //emit json
            final JSONObject res = pojo.asJSON();

            //now check againt on emitted
            for (final Map.Entry<TestFieldId, Object> e : vals.entrySet()) {
                final String key = e.getKey().jsonName;
                final Object o = e.getValue();

                final Object this_val = res.get(key);
                if (this_val instanceof URI || o instanceof URI) {
                    assertEquals("Object " + key + " does not match",
                                 this_val.toString(),
                                 o.toString());
                } else {
                    assertEquals("Object " + key + " does not match",
                                 this_val,
                                 o);
                }
            }
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }

    public static <T extends JSONEntity> void checkFromJSON(Class c,
                                                              Map<TestFieldId, Object> vals) {
        final String FN_NAME =
                MethodHandles.lookup().lookupClass() + ".checkFromJSON()";
        try {
            final T pojo = (T)c.newInstance();
            final JSONObject o = new JSONObject();
            for (final Map.Entry<TestFieldId, Object> e : vals.entrySet()) {
                o.put(e.getKey().jsonName, e.getValue());
            }

            pojo.fromJSON(o);

            for (final Map.Entry<TestFieldId, Object> e : vals.entrySet()) {
                final Field f = c.getDeclaredField(e.getKey().fieldName);
                f.setAccessible(true);
                final Object this_val = f.get(pojo);

                assertEquals(
                        "Object " + e.getKey().fieldName + " does not match",
                        this_val,
                        e.getValue());
            }
        } catch (Exception e) {
            System.err.println(FN_NAME + " : " + e);
            fail(e.getMessage());
        }
    }
}
