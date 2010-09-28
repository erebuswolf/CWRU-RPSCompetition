#!/usr/bin/perl
# Team: Tuna Fish French Fry
use strict;
use warnings;
use IO::Socket;
use IO::Socket::Multicast;

die "Usage: pigeon_milk PORT\n" if @ARGV!=1;

my ($servername,$port)=('localhost',$ARGV[0]);
my ($name,$opname)='pigeon_milk';
my (%movebytetoint,@moveinttobyte);
my ($reqsig,$clientsig,$shutdownsig,$ressig);
open F,'>>pigeon_milk.txt' or die $!;



# code for making decisions

my $gamestate='';	# {opmove, mymove}...
my %gamepred=();	# {opmove, mymove}..., opnextmove
my ($gamelen,$maxlen)=(0,100);
my ($wins,$losses,$ties)=(0,0,0);

sub recmoves{	# mymove, opmove
	my $throw=$_[1].$_[0];
	my $res={'13'=>-1,'21'=>-1,'32'=>-1,'12'=>1,'23'=>1,'31'=>1}->{$throw};
	$res=0 if !defined $res;
	print F "I played $_[0], op played $_[1]\nI ".
		['lost','tied','won']->[$res+1]."\n\n";
	if($res==1){ $wins++; }
	elsif($res==-1){ $losses++; }
	else{ $ties++; }
	$gamepred{substr($gamestate,-2*$_).$_[1]}++ for 1..$gamelen;
	$gamestate.=$throw;
	$gamelen++ if $gamelen<$maxlen;
}

sub getmove{
	my $wtsum=0;
	my @opmovewts=map{
		my ($ret,$guessopmove)=(0,$_);
		$ret+=($gamepred{substr($gamestate,-2*$_).$guessopmove} or 0)
			for 1..$gamelen;
		$wtsum+=$ret;
		$ret;
	} 1..3;
	$wtsum=1 if $wtsum<1;
	print F join(', ',@opmovewts)." => ".
		join(', ',map sprintf('%.5f',$_/$wtsum), @opmovewts)."\n";
	my $retwt=[sort @opmovewts]->[-1];
	my @rets=grep $opmovewts[$_]==$retwt,0..2;
	$rets[0]=$rets[int(@rets*rand)] if @rets>1;
	print F "Op play ".($rets[0]+1)."?\n";
	return [2,3,1]->[$rets[0]];	
}

sub endcode{
	print F "Op: $opname\nWins $wins\nLoss $losses\nTies $ties\n\n\n\n\n\n";
	close F;
}




# connect to server and get info
my $sock=new IO::Socket::INET(PeerAddr=>'127.0.0.1',
	PeerPort=>$port,Proto=>'tcp') or die $!;
sub dorecv{
	my ($ret,$s)='';
	while(length $ret<$_[0]){
		die $! if !sysread $sock,$s,$_[0]-length $ret;
		$ret.=$s;
	}
	return $ret;
}
sub sendstr{ die $! if !defined syswrite $sock,chr(length $_[0]).$_[0]; }
sub recvstr{ return dorecv(ord dorecv(1)); }

sendstr($name);
$opname=recvstr();
my ($broadcastip,$broadcastport)=(recvstr(),0);
$broadcastport=$broadcastport*256+ord($_) for split //,recvstr();
$movebytetoint{$moveinttobyte[$_]=dorecv(1)}=$_ for 0..3;
($reqsig,$clientsig,$shutdownsig,$ressig)=map dorecv($_),(2,1,2,1);
shutdown $sock,2;
close $sock;




# start multicast and play game
my $sock2=new IO::Socket::Multicast(LocalPort=>$broadcastport,ReuseAddr=>1);
die $! if !$sock2;
$sock2->mcast_add($broadcastip);
for(;;){
	my $packet;
	my $nr=sysread $sock2,$packet,4;
	if($nr==2){
		if($packet eq $reqsig){
			$sock2->mcast_send($moveinttobyte[getmove()].$clientsig,
				"$broadcastip:$broadcastport");
		}elsif($packet eq $shutdownsig){
			shutdown $sock2,2;
			close $sock2;
			endcode();
			exit;
		}
	}elsif($nr==3 and $ressig eq substr($packet,-1))
		{ recmoves(map $movebytetoint{substr $packet,$_,1},0..1); }
}
