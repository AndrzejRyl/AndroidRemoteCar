import socket
import RPi.GPIO as GPIO
import time

''' 
Setup output pins
'''
forw = 26
backw = 6
left = 20
right = 21
light1 = 4
light2 = 16
lightsOn = False
freq = 200

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(forw, GPIO.OUT)
GPIO.setup(backw, GPIO.OUT)
GPIO.setup(left, GPIO.OUT)
GPIO.setup(right, GPIO.OUT)
GPIO.setup(light1, GPIO.OUT)
GPIO.setup(light2, GPIO.OUT)
forward = GPIO.PWM(forw, freq)
backward = GPIO.PWM(backw, freq)
#leftt = GPIO.PWM(left, freq)
#rightt = GPIO.PWM(right, freq)
GPIO.output(light1, lightsOn)
GPIO.output(light2, lightsOn)

''' 
Define steering functions
'''
def goForward(speed):
	backward.stop()
	time.sleep(0.5)
	forward.start(speed)

def goBackward(speed):
	forward.stop()
	time.sleep(0.5)
	backward.start(speed)

def stop():
	forward.stop()
	backward.stop()

def turnLeft():
	#rightt.stop()
	#leftt.start(90)
	GPIO.output(right, False)
	GPIO.output(left, True)

def turnRight():
	#leftt.stop()
	#rightt.start(90)
	GPIO.output(left, False)
	GPIO.output(right, True)

def straight():
	#rightt.stop()
	#leftt.stop()
	#time.sleep(0.5)
	#rightt.stop()
	GPIO.output(left, False)
	GPIO.output(right, False)

def toggleLights(lightsOn):
	GPIO.output(light1, lightsOn)
	GPIO.output(light2, lightsOn)

''' 
Setup socket connection
'''
HOST = ''
PORT = 8988
soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
soc.bind((HOST, PORT))

''' 
Listen for incoming messages
'''
soc.listen(1)
conn, addr = soc.accept()
while 1:
	data = conn.recv(1024)
	if not data: continue
	#print '   ', data
	msg = str(data).upper()
	if msg[0] == 'L':
		lightsOn = not lightsOn
		toggleLights(lightsOn)
	else: 
		if msg[0] == '-':
			stop()
		elif msg[0] in ('F', 'B'):
			speed = float(data[2]) * 20 + 40
			if msg[0] == 'F':
				goForward(speed)
			elif msg[0] == 'B':
				goBackward(speed)
		if msg[1] == '-':
			straight()
		elif msg[1] == 'R':
			turnRight()
		elif msg[1] == 'L':
			turnLeft()
conn.close
