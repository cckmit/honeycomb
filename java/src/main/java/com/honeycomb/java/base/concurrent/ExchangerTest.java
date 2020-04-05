package com.honeycomb.java.base.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * @author maoliang
 * @className ExchangerTest
 * @date 2019-03-01 16:31
 */
public class ExchangerTest {

    static class Producer implements Runnable {

        private final List<String> buffer;

        private final Exchanger<List<String>> exchanger;

        public Producer(List<String> buffer, Exchanger<List<String>> exchanger) {
            this.buffer = buffer;
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            for (int i = 1; i < 5; i++) {
                System.out.println("生产者第" + i + "次提供");
                for (int j = 1; j < 3; j++) {
                    System.out.println("生产者装入" + i + "--" + j);
                    buffer.add("buffer:" + i + "--" + j);
                }
//                System.out.println("生产者buffer" + buffer.hashCode());
                System.out.println("交换数据前，buffer中有" + buffer.size() + "个");
                System.out.println("生产者装满，等待与消费者交换。。。");
                try {
                    exchanger.exchange(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer implements Runnable {

        private List<String> buffer;

        private final Exchanger<List<String>> exchanger;

        public Consumer(List<String> buffer, Exchanger<List<String>> exchanger) {
            this.buffer = buffer;
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            for (int i = 1; i < 5; i++) {
                try {
                    buffer = exchanger.exchange(buffer);
//                    System.out.println("消费者buffer" + buffer.hashCode());
                    System.out.println("交换数据后，buffer中有" + buffer.size() + "个");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("消费者第" + i + "次提取");
                for (int j = 1; j < 3; j++) {
                    System.out.println("消费者：" + buffer.get(0));
                    buffer.remove(0);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> buffer1 = new ArrayList<String>();
        List<String> buffer2 = null;

        Exchanger<List<String>> exchanger = new Exchanger<List<String>>();

        Thread producerThread = new Thread(new Producer(buffer1, exchanger));
        Thread consumerThread = new Thread(new Consumer(buffer2, exchanger));

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();

        System.out.println("ok");
    }
}
