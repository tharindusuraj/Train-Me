from sys import platform
from os import system
from imp import load_source
from os.path import join

class readTarget:

        wlbt = None
        
        def __init__(self, gui):
            global wlbt
            self.__gui = gui
            
            if platform == 'win32':
                modulePath = join('WalabotAPI.py')
            elif platform.startswith('linux'):
                modulePath = join('/usr', 'share', 'walabot', 'python', 'WalabotAPI.py')     
            
            wlbt = load_source('WalabotAPI',modulePath)
            print("test")
            wlbt.Init()
            print("INIT")
            
                            
        def SensorApp(self):
            global wlbt
            
            f = open("Data.txt","r");
            data = f.readlines();
            
            minInCm, maxInCm, resInCm = int(data[0]),int(data[1]),int(data[2])
            minIndegrees, maxIndegrees, resIndegrees = int(data[3]),int(data[4]),int(data[5])
            minPhiInDegrees, maxPhiInDegrees, resPhiInDegrees = int(data[6]),int(data[7]),int(data[8])
            Tres = int(data[9])
            mtiMode = bool(data[9])
            
            wlbt.SetSettingsFolder()
            # 1) Connect : Establish communication with walabot.
            wlbt.ConnectAny()
            # 2) Configure: Set scan profile and arena
            # Set Profile - to Sensor.
            wlbt.SetProfile(wlbt.PROF_SENSOR)
            # Setup arena - specify it by Cartesian coordinates.
            wlbt.SetArenaR(minInCm, maxInCm, resInCm)
            # Sets polar range and resolution of arena (parameters in degrees).
            wlbt.SetArenaTheta(minIndegrees, maxIndegrees, resIndegrees)
            # Sets azimuth range and resolution of arena.(parameters in degrees).
            wlbt.SetThreshold(Tres)
            wlbt.SetArenaPhi(minPhiInDegrees, maxPhiInDegrees, resPhiInDegrees)
            # Moving Target Identification: standard dynamic-imaging filter
            filterType = wlbt.FILTER_TYPE_MTI if mtiMode else wlbt.FILTER_TYPE_NONE
            wlbt.SetDynamicImageFilter(filterType)
            # 3) Start: Start the system in preparation for scanning.
            wlbt.Start()
            if not mtiMode: # if MTI mode is not set - start calibrartion
                # calibrates scanning to ignore or reduce the signals
                wlbt.StartCalibration()
                while wlbt.GetStatus()[0] == wlbt.STATUS_CALIBRATING:
                    wlbt.Trigger()
            return "DONE"
        
        def coordinates(self):
                global wlbt
                while 1:
                    appStatus, calibrationProcess = wlbt.GetStatus()
                    # 5) Trigger: Scan(sense) according to profile and record signals
                    # to be available for processing and retrieval.
                    wlbt.Trigger()
                    # 6) Get action: retrieve the last completed triggered recording
                    targets = wlbt.GetSensorTargets()
                    rasterImage, _, _, sliceDepth, power = wlbt.GetRawImageSlice()
                    #PrintSensorTargets(targets)
                    
                    if targets:            
                            for i, target in enumerate(targets):
                                tempz = round(target.zPosCm,0)
                                tempy = round(target.xPosCm,0)
                                print(tempz,tempy)
                                f = open("output.txt","w")
                                f.write("%d %d" % (tempz,tempy))
                                f.close()
                                break
                            

        def disconnect(self):     
            # 7) Stop and Disconnect.
            wlbt.Stop()
            wlbt.Disconnect()
            print('Terminate successfully')


rt = readTarget(None)

rt.SensorApp()

rt.coordinates()
