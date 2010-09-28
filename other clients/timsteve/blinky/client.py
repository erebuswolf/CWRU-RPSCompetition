
import time, thread, socket, platform, textwrap, sys, struct, os
from crypt_framework.CommGenerics import SocketGeneric, UDPGeneric
from crypt_framework.ClientGeneric import ClientGeneric
from crypt_framework.CommunicationLink import CommunicationLink
from crypt_framework.CommandProcessors import ClientCommandProcessor
from crypt_framework.dec_factories import create_existance_check_dec, create_value_check_dec

INFO_SIZE = 10

rand_data = os.urandom(1000)
rand_loc = 0

class Info(object):

    def __init__(self, s, opponent_name, addr, port):
        assert len(s) == INFO_SIZE
        #print s, len(s)
        self.opponent_name = opponent_name
        self.addr = addr
        self.port = port
        self.garbage = s[0]
        self.rock = s[1]
        self.paper = s[2]
        self.scissors = s[3]
        self.reqsig = s[4:6]
        self.clisig = s[6]
        self.downsig = s[7:9]
        self.ressig = s[9]

    def __str__(self):
        s = ''
        for attr in dir(self):
            if attr[0] != '_':
                a = getattr(self, attr)
                if len(a) == 1: a = ord(a)
                s += '%s = "%s"\n' %(attr, a)
        return s

class RPSClientProcessor(ClientCommandProcessor):
    '''
    Defines how the smtp client handles the server response.
    '''
    def __init__(self, client): self.client = client

    def exec_command(self, cmd, msg, sig):
        '''
        In client simply translates the response code, cmd, into an integer and calls the SmtpLink.next function.
        That function holds the equations for the statemachine transition.
        '''
        #if cmd.isdigit(): cmd = int(cmd)
        self.client.link.next(cmd, msg)

class RPSLink(CommunicationLink):
    '''
    Defines the smtp protocol.
    '''

    def __init__(self, comm):
        '''
        comm is the CommunicationGeneric
        '''
        self.comm = comm
        self.ready = False
        self.state = 0
        self.accepted_rcpt = 0
        self.wait = True
        self.opponent_name = ''

    def send(self, msg):
        '''
        defines how the link sends a message
        '''
        self.comm.send(msg)

    def process(self, data):
        '''
        defines how the servers responses should be processed. I split them into cmd, msg, None where
        cmd is actually the server response code, msg is the informational message the server provides.
        '''
        #split = data.split(' ', 1)
        return data, '', None

    def next(self, val, msg=''):
        '''
        The transition function for the smtp link state machine.

        val is the input that detirmines the next state.
        msg is just informational (usually only used when there is an error).
        '''
        #print 'state', self.state
        self.wait = False
        if self.state == 0:
            self.state = 1
            self.acc = ord(val[0])
            self.field = 'opponent_name'
            self.next_state = 2
            self.m = ''
        elif self.state == 1:
            self.m += val
            self.acc -= len(val)
            if self.acc <= 0:
                setattr(self, self.field, self.m)
                self.m = None
                print "%s = '%s'" % (self.field, getattr(self, self.field))
                print [ord(c) for c in getattr(self, self.field)]
                self.state = self.next_state
        elif self.state == 2:
            self.state = 1
            self.acc = ord(val[0])
            self.field = 'addr'
            self.next_state = 3
            self.m = ''
        elif self.state == 3:
            self.state = 4
            self.acc = ord(val[0])
            self.field = 'port'
            self.next_state = 5
            self.m = 0
        elif self.state == 4:
            self.m = self.m<<8
            self.m |= ord(val)
            self.acc -= len(val)
            if self.acc <= 0:
                setattr(self, self.field, str(self.m))
                self.m = None
                print "%s = '%s'" % (self.field, getattr(self, self.field))
                self.state = self.next_state
        elif self.state == 5:
            self.state = 1
            self.acc = INFO_SIZE
            self.field = 'info'
            self.next_state = 6
            self.m = ''
            self.next(val, msg)
        elif self.state == 6:
            self.info = Info(self.info, self.opponent_name, self.addr, self.port)
        else:
            print 'unknown state', self.state
            self.failure(msg)



    def recieve(self):
        '''
        defines how the link recieves a message
        '''
        return self.process(self.comm.recieve())

    def failure(self, msg):
        '''
        the link has failed. This could be for a legitimate reason (for instance the link has been closed).
        However, it could also be from an error. This function hard closes the CommGeneric and sets the
        state to undefined.
        '''
        print 'failure>', msg
        self.state = None
        self.comm.close()

    def quit(self):
        '''quits the connection'''
        self.wait = True
        self.failure('quitting')

class RPSClient(ClientGeneric):
    '''
    The actual network client. Uses all of the above to define itself.
    '''

    def __init__(self, host, port=25, bufsize=1024):
        '''
        Constructs the client by having it connect to the smtp server specified by host, port.
        '''
        self.HOST = host
        self.PORT = port
        self.BUFSIZE = bufsize
        self.ADDR = (self.HOST, self.PORT)
        self.stop = False
        self.lock = None

        self.commGeneric = SocketGeneric(host, port, bufsize, END_MARK='')
        self.link = RPSLink(self.commGeneric)

        sys_processor = ClientCommandProcessor()
        usr_processor = RPSClientProcessor(self)

        sys_processor.init(self.link, self)
        usr_processor.init(self.link, self)

        def commands(data):
            try: cmd, msg, sig = self.link.process(data)
            except Exception, e:
                print e
                raise
            usr_processor.exec_command(cmd, msg, sig)

        self.commGeneric.set_proc_command(commands)
        self.activateClient()

    def __del__(self):
        self.deactivateClient()

    def exit(self): self.close()

    def activate(self):
        '''activates the client for sending'''
        ## waits while the server sends the welcome message ##
        self.link.send(chr(6))
        self.link.send('blinky')
        while self.link.state != 6: pass
        self.link.next(None)
        self.info = self.link.info
        #print self.info
        print 'activated'

    def listen(self, lock=False):
        '''
        the thread which listens to the server for messages. Note it doesn't call the
        command processors. The communication generic does that. This is why the method
        seems to do nothing. But actually it defines how, and when the recieve method gets
        called.
        '''
        print 'listening'
        while not self.commGeneric.closed:
            cmd, msg, sig = self.link.recieve()
            #print cmd
        if lock: lock.release()
        self.exit()

    def activateClient(self):
        '''
        API method for turning the client on.
        '''
        self.connect()

        self.lock = lock = thread.allocate_lock()
        lock.acquire()
        thread.start_new_thread(self.listen, (lock,))
        self.activate()

    def stopListening(self):
        '''defines how to stop listening.'''
        self.link.quit()
        while self.link.state: pass

    def close(self):
        '''defines how to close the link'''
        self.deactivateClient()

def getinfo():
    rps = RPSClient('127.0.0.1', port=int(sys.argv[1]))
    rps.stopListening()
    rps.close()
    return rps.info

def get_result(byte):
    if byte == info.garbage: return "garbage"
    elif byte == info.rock: return "rock"
    elif byte == info.scissors: return "scissors"
    elif byte == info.paper: return "paper"
    else: return "bad packet"

def play(throw):

    udp = UDPGeneric(info.addr, int(info.port))
    r = udp.recieve()
    p = None
    #print get_result(r[0])
    while 1:
        r = udp.recieve()
        if len(r) == 2:
            #//possible request or shutdown
            if r == info.reqsig:
                #r = udp.recieve()
                #while len(r) != 2:
                    #r = udp.recieve()
                #print r, get_result(r[0]), get_result(r[1]), [ord(c) for c in r]
                #for x in range(256):
                    #if chr(x) == info.clisig: continue
                    #udp.send(info.garbage+chr(x))
                t = throw(udp, p)
                if t == "rock": byte = info.rock
                elif t == "scissors": byte= info.scissors
                elif t == "paper": byte = info.paper
                else: byte = info.garbage
                udp.send(byte+info.clisig)
            elif r == info.downsig:
                #print 'SHUTDOWN'
                break
                #print [ord(c) for c in r], [ord(c) for c in info.reqsig], [ord(c) for c in info.downsig]
        elif len(r) == 3:
            #print '3>', r, r[2] == info.ressig
            if r[2] == info.ressig:
                #print (get_result(r[0]), get_result(r[1]))
                p = get_result(r[1])
        #else:
            #raise Exception, "Fuck"
    udp.close()
    print 'done'

class A(object):

    def __init__(self):
        self.sig = None
        self.rock = None
        self.paper = None
        self.scissors = None
        self.prev = None
        self.done = False
        self.redo = False

    def thrower(self, udp, p):
        global rand_loc
        if not self.done:
            r = udp.recieve()
            if self.redo:
                r = udp.recieve()
                #r = udp.recieve()
            ##else:
            #while len(r) < 2:
                #r = udp.recieve()
            #print r, get_result(r[0]), get_result(r[1]), [ord(c) for c in r]
            if self.sig is None: self.sig = r[1]
            if p != None and self.prev != None:
                if p == "rock": self.rock = self.prev
                elif p == "scissors": self.scissors = self.prev
                elif p == "paper": self.paper = self.prev
            if self.prev != None and self.rock == self.scissors and self.scissors == self.paper:
                #try:
                    #print "sig", ord(self.sig)
                    #print "rock", ord(self.rock)
                    #print "paper", ord(self.paper)
                    #print "scissors", ord(self.scissors)
                #except: pass
                self.redo = True
                self.sig = None
                self.rock = None
                self.paper = None
                self.scissors = None
                udp.send(rand_data[1:5])
            elif (self.sig is not None and self.rock is not None and
                self.paper is not None and self.scissors is not None):
                    self.done = True
                    #print "sig", ord(self.sig)
                    #print "rock", ord(self.rock)
                    #print "paper", ord(self.paper)
                    #print "scissors", ord(self.scissors)
            self.prev = str(r[0])
        else:
            udp.send(self.rock+self.sig)
            return "paper"
        #first = False
        #print p
        i = ord(rand_data[rand_loc])%3
        rand_loc += 1
        if i == 0: return "rock"
        elif i == 1: return "paper"
        else: return "scissors"

if __name__ == '__main__':
    ''' testing code '''
    info = getinfo()
    #print rps.link.infoimport socket



    play(A().thrower)

