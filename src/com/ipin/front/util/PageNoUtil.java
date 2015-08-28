package com.ipin.front.util;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

/**

 *����������Ҫ����ʵ�ַ�ҳ

 */

public class PageNoUtil {

/**

     * @param             session :һ���Ự

     * @param            hql:����Ҫִ�е�hql��䣬

     * @param            offset ���ÿ�ʼλ��

     * @param              length:��ȡ��¼����

     * return             ���ؽ����List<?>��ʾһ�����͵�List

     */

    public static List<?> getList( Session session , String hql , int pageNum){

       Query q = session.createQuery(hql);

       q.setFirstResult((pageNum-1)*20);

       q.setMaxResults(20);

       List<?> list = q.list();

       return list;

    }

}