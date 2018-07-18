import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) throws Exception {

        new ColorPicky();
        //Thread.sleep(4000);
    }


    public static void startProducerConsumer() {
        BlockingQueue queue = new ArrayBlockingQueue(1024);
        LinkedBlockingQueue publisherQueue = new LinkedBlockingQueue<byte[]>(1024);

        ReadFrameProducer producer = new ReadFrameProducer(queue, ColorPicky.red, ColorPicky.green, ColorPicky.blue);
        FramesConsumer consumer = new FramesConsumer(queue, publisherQueue);


        new Thread(producer).start();
        new Thread(consumer).start();
    }

}
