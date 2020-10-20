package guice.unsingleton;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @ClassName: TestSampleWithoutSingleton
 * @Description:
 * 如果我们不用Singleton标注，每次获取实例时，Guice会重新构造一个，这个会有反射构造器的性能损耗，在高性能场景下，请谨慎。
 * @Author: VT
 * @Date: 2020-10-20 20:03
 */
class HelloPrinter {

    private static int counter;

    private int myCounter;

    public HelloPrinter() {
        myCounter = counter++;
    }

    public void print() {
        System.out.printf("Hello, World %d\n", myCounter);
    }

}

public class TestSampleWithoutSingleton {

    @Inject
    private HelloPrinter printer;

    public void hello() {
        printer.print();
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        TestSampleWithoutSingleton sample = injector.getInstance(TestSampleWithoutSingleton.class);
        sample.hello();
        sample = injector.getInstance(TestSampleWithoutSingleton.class);
        sample.hello();
        sample = injector.getInstance(TestSampleWithoutSingleton.class);
        sample.hello();
        sample = injector.getInstance(TestSampleWithoutSingleton.class);
        sample.hello();
    }

}
