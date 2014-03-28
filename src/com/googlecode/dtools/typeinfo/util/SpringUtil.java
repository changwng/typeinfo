package com.googlecode.dtools.typeinfo.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 07.06.11
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
public class SpringUtil {

    static ApplicationContext applicationContext;


//        public static ApplicationContext getApplicationContext() {
//        if (applicationContext == null)
//            applicationContext = new ClassPathXmlApplicationContext("typeinfo.config.xml");
//        return applicationContext;
//    }

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null)
            try {
                applicationContext = new FileSystemXmlApplicationContext("typeinfo.config.xml");
            } catch (BeansException e) {
                System.err.println("Cannot find config with FileSystemXmlApplicationContext ");
                applicationContext = new ClassPathXmlApplicationContext("typeinfo.config.xml");
            }
        return applicationContext;
    }

    public static Object getSpringBean(String beanName){
        return (Object) getApplicationContext().getBean(beanName);
    }
}
