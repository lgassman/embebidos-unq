package test.icecapvm.minitests;

import javax.realtime.AperiodicParameters;
import javax.realtime.Clock;
import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.realtime.RelativeTime;
import javax.safetycritical.AperiodicEventHandler;
import javax.safetycritical.Launcher;
import javax.safetycritical.Mission;
import javax.safetycritical.MissionSequencer;
import javax.safetycritical.PeriodicEventHandler;
import javax.safetycritical.Safelet;
import javax.safetycritical.StorageParameters;
import javax.scj.util.Const;
import javax.scj.util.Priorities;

public class TestSCJPrioritySchedule2 {

    private static int testCount;

    static {
        testCount = 0;
    }

    private static class MyPeriodicEvh extends PeriodicEventHandler {
        int n;
        AperiodicEventHandler aevh;

        int count = 0;

        protected MyPeriodicEvh(PriorityParameters priority, PeriodicParameters periodic, long memSize, int n, AperiodicEventHandler aevh) {
            super(priority, periodic, storageParameters);

            this.n = n;
            this.aevh = aevh;
        }

        public void handleAsyncEvent() {
            count++;

            for (int i = 0; i < n; i++) {
                new Integer(count);
            }
            if (count % 4 == 3 && n == 2)
                aevh.release();
        }
    }

    private static class MyAperiodicEvh extends AperiodicEventHandler {
        int n;
        MissionSequencer<MyMission> missSeq;
        int count = 0;

        public MyAperiodicEvh(PriorityParameters priority, AperiodicParameters release, long memSize, int n, MissionSequencer<MyMission> missSeq) {
            super(priority, release, storageParameters);
            this.n = n;
            this.missSeq = missSeq;
        }

        public void handleAsyncEvent() {
            count++;

            for (int i = 0; i < n; i++) {
                new Integer(count);
            }
            testCount++;
            missSeq.requestSequenceTermination();
        }
    }

    private static class MyMission extends Mission {
        MissionSequencer<MyMission> missSeq;

        public MyMission(MissionSequencer<MyMission> missSeq) {
            this.missSeq = missSeq;
        }

        public void initialize() {

            AperiodicEventHandler aevh = new MyAperiodicEvh(new PriorityParameters(Priorities.PR98), new AperiodicParameters(new RelativeTime(50, 0, Clock.getRealtimeClock()), null), 64, 1, missSeq);
            aevh.register();

            PeriodicEventHandler pevh1 = new MyPeriodicEvh(new PriorityParameters(Priorities.PR97), new PeriodicParameters(new RelativeTime(Clock.getRealtimeClock()), // start
                    new RelativeTime(100, 0, Clock.getRealtimeClock())), // period
                    64, // size of private mem
                    2, aevh); // used in pevh.handleAsyncEvent
            pevh1.register();
        }

        public long missionMemorySize() {
            /* Not used right now ??? */
            return 0;
        }
    }

    private static class MyApp implements Safelet<MyMission> {
        public MissionSequencer<MyMission> getSequencer() {
            // devices.Console.println("   ** MyApp.getSequencer");
            return new MySequencer();
        }

        public long immortalMemorySize() {
            return Const.IMMORTAL_MEM_SIZE;
        }

        private class MySequencer extends MissionSequencer<MyMission> {
            private MyMission mission;

            MySequencer() {
                super(new PriorityParameters(Priorities.PR95), storageParameters); // mission
                mission = new MyMission(this);
            }

            public MyMission getNextMission() {
                if (mission.terminationPending()) {
                    // devices.Console.println("\nMySeq.getNextMission:null; missionTerminate:"
                    // + mission.terminationPending() + "\n");
                    return null;
                } else {
                    // devices.Console.println("\nMySeq.getNextMission:" +
                    // mission + "\n");
                    return mission;
                }
            }
        }
    }

    static StorageParameters storageParameters;

    /**
     * Compiling for the PC:
     * 
     * gcc -Wall -pedantic -g -Os -DPC64 -DPRINTFSUPPORT -DJAVA_STACK_SIZE=768
     * -DJAVA_HEAP_SIZE=15360 classes.c icecapvm.c methodinterpreter.c methods.c
     * gc.c natives_allOS.c natives_i86.c rom_heap.c allocation_point.c
     * rom_access.c x86_64_interrupt.s native_scj.c -lpthread -lrt
     * 
     * @param args
     */
    public static void main(String[] args) {

        storageParameters = 
          new StorageParameters(Const.BACKING_STORE_SIZE, 
                  new long[] { Const.HANDLER_STACK_SIZE }, 
                  Const.PRIVATE_MEM_SIZE, Const.IMMORTAL_MEM_SIZE, Const.MISSION_MEM_SIZE);

        devices.Console.println("********** main.begin ******************");
        new Launcher(new MyApp(), 1);
        devices.Console.println("********* main.end ************************");
        if (testCount == 1) {
            args = null;
        }
    }
}
