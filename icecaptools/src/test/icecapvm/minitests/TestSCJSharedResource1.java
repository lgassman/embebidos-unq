package test.icecapvm.minitests;

/**************************************************************************
 * File name  : TestSCJSharedResource1.java
 * 
 * This code is available under the license:
 * Creative Commons, http://creativecommons.org/licenses/by-nc-nd/3.0/
 * It is free for non-commercial use. 
 * 
 * VIA University College, Horsens, Denmark, 2014
 * Hans Soendergaard, hso@viauc.dk
 * 
 * Description: 
 *************************************************************************/

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
import javax.safetycritical.Services;
import javax.safetycritical.StorageParameters;
import javax.scj.util.Const;
import javax.scj.util.Priorities;

public class TestSCJSharedResource1 {
    static boolean failed;

    private static class MyPeriodicEvh extends PeriodicEventHandler {
        int n;
        AperiodicEventHandler aevh1;
        AperiodicEventHandler aevh2;

        Resource res;

        int count = 0;

        protected MyPeriodicEvh(PriorityParameters priority, PeriodicParameters periodic, long memSize, // size
                                                                                                        // of
                                                                                                        // private
                                                                                                        // mem
                int n, Resource res, AperiodicEventHandler aevh1, AperiodicEventHandler aevh2) {
            super(priority, periodic, storageParameters);
            this.n = n;
            this.res = res;
            this.aevh1 = aevh1;
            this.aevh2 = aevh2;
        }

        public void handleAsyncEvent() {
            devices.Console.println("MyPEvh " + n + "; count " + count);
            res.inc();
            res.dec();

            int value = res.get();

            // Invariant: 0 <= value < numberOfPeriodicEventHandlers
            if (!invariant(value))
                aevh1.release();

            count++;
            if (n == 2 && count == 5) // 1000
            {
                devices.Console.println("MyPEvh " + n + "; count " + count + "; aevh2.register");
                aevh2.release();
            }
        }

        private boolean invariant(int inv) {
            return (0 <= inv) && (inv < MyMission.NumberOfPeriodicEvhs);
        }
    }

    private static class MyAperiodicEvh1 extends AperiodicEventHandler {
        MissionSequencer<MyMission> missSeq;

        Resource res;

        public MyAperiodicEvh1(PriorityParameters priority, AperiodicParameters release, long memSize, // size
                                                                                                       // of
                                                                                                       // private
                                                                                                       // memory
                MissionSequencer<MyMission> missSeq, Resource res) {
            super(priority, release, storageParameters);
            this.missSeq = missSeq;
            this.res = res;
        }

        public void handleAsyncEvent() {
            devices.Console.println("--> MyAPEvh: inv broken: " + res.get());
            failed = true;
            missSeq.requestSequenceTermination();
        }
    }

    private static class MyAperiodicEvh2 extends AperiodicEventHandler {
        Mission mission;

        public MyAperiodicEvh2(PriorityParameters priority, AperiodicParameters release, long memSize, // size
                                                                                                       // of
                                                                                                       // private
                                                                                                       // memory
                Mission m) {
            super(priority, release, storageParameters);
            this.mission = m;
        }

        public void handleAsyncEvent() {
            failed = false;
            mission.requestTermination();
        }
    }

    private static class Resource {
        private int value = 0;

        private final int n = 2;

        public synchronized void inc() {
            int local = value;
            // To consume some time:
            for (int i = 0; i < 2 * n; i++) {
                local = ~local; // bitwise complement
            }
            // increment
            value = local + 1;
        }

        public synchronized void dec() {
            int local = value;
            for (int i = 0; i < 2 * n; i++) {
                local = ~local; // bitwise complement
            }

            value = local - 1;
        }

        public synchronized int get() {
            return value;
        }
    }

    private static class MyMission extends Mission {
        static final int NumberOfPeriodicEvhs = 2;

        MissionSequencer<MyMission> missSeq;

        public MyMission(MissionSequencer<MyMission> missSeq) {
            this.missSeq = missSeq;
        }

        public void initialize() {
            Resource res = new Resource();

            AperiodicEventHandler aevh1 = new MyAperiodicEvh1(new PriorityParameters(Priorities.PR98), new AperiodicParameters(new RelativeTime(500, 0, Clock.getRealtimeClock()), null), Const.PRIVATE_MEM_SIZE, missSeq, res);
            aevh1.register();

            AperiodicEventHandler aevh2 = new MyAperiodicEvh2(new PriorityParameters(Priorities.PR98), new AperiodicParameters(new RelativeTime(500, 0, Clock.getRealtimeClock()), null), Const.PRIVATE_MEM_SIZE, this);
            aevh2.register();

            new MyPeriodicEvh(new PriorityParameters(Priorities.PR96), new PeriodicParameters(new RelativeTime(Clock.getRealtimeClock()), // start
                    new RelativeTime(1000, 0, Clock.getRealtimeClock())), // period
                    Const.PRIVATE_MEM_SIZE, // size of private mem
                    2, res, aevh1, aevh2).register();

            new MyPeriodicEvh(new PriorityParameters(Priorities.PR96), new PeriodicParameters(new RelativeTime(200, 0, Clock.getRealtimeClock()), // start
                    new RelativeTime(1000, 0, Clock.getRealtimeClock())), // period
                    Const.PRIVATE_MEM_SIZE, // size of private mem
                    3, res, aevh1, null).register();

            devices.Console.println("MyMission: Services.setCeiling");
            int ceiling = Priorities.PR96;
            Services.setCeiling(res, ceiling);
        }

        public long missionMemorySize() {
            return Const.MISSION_MEM_SIZE;
        }
    }

    private static class MyApp implements Safelet<MyMission> {
        public MissionSequencer<MyMission> getSequencer() {
            devices.Console.println("MyApp.getSequencer");
            return new MySequencer();
        }

        public long immortalMemorySize() {
            return Const.IMMORTAL_MEM_SIZE;
        }

        private class MySequencer extends MissionSequencer<MyMission> {
            private MyMission mission;
            private int howManyTimes;

            MySequencer() {
                super(new PriorityParameters(Priorities.PR95), storageParameters); // mission
                                                                                                                                                              // memory
                                                                                                                                                              // size

                this.mission = new MyMission(this);
                this.howManyTimes = 0;
            }

            public MyMission getNextMission() {
                howManyTimes++;
                if (howManyTimes > 3 || mission.terminationPending()) {
                    return null;
                } else {
                    return mission;
                }
            }
        }
    }

    static StorageParameters storageParameters;
 
    public static void main(String[] args) {

        storageParameters = 
          new StorageParameters(Const.BACKING_STORE_SIZE, 
                  new long[] { Const.HANDLER_STACK_SIZE }, 
                  Const.PRIVATE_MEM_SIZE, Const.IMMORTAL_MEM_SIZE, Const.MISSION_MEM_SIZE);

        devices.Console.println("\n********** Shared Resource 1 main.begin ***********");
        // executes in heap memory
        new Launcher(new MyApp(), 1);
        devices.Console.println("********* Shared Resource 1 main.end *****************");

        if (!failed) {
            args = null;
        }
    }

}
