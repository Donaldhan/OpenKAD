package guice.singleton;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * @ClassName: TestSample
 * @Description:
 * @Author: VT
 * @Date: 2020-10-20 20:05
 */
@Singleton
class HelloPrinter {

    public void print() {
        System.out.println("Hello, World");
    }

}

@Singleton
public class TestSample {

    @Inject
    private HelloPrinter printer;

    public void hello() {
        printer.print();
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        TestSample sample = injector.getInstance(TestSample.class);
        sample.hello();
    }

}
