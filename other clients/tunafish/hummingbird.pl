#!/usr/bin/perl
# Team: Tuna Fish French Fry
use strict;
use warnings;
use IO::Socket;
use IO::Socket::Multicast;

die if @ARGV!=1;
my ($qs,$cs,$ss,$rs);
my $s1=new IO::Socket::INET(PeerAddr=>'127.0.0.1',
	PeerPort=>$ARGV[0],Proto=>'tcp') or die;
sub dorecv{
	my ($ret,$s)='';
	while(length $ret<$_[0]){die if !sysread $s1,$s,$_[0]-length $ret;$ret.=$s;}
	return $ret;
}
sub sstr{ die $! if !defined syswrite $s1,chr(length $_[0]).$_[0]; }
sub rstr{ return dorecv(ord dorecv(1)); }
sstr('hummingbird');
rstr();
my ($bip,$bp)=(rstr(),0);
$bp=$bp*256+ord($_) for split //,rstr();
my @a=map dorecv(1), 0..3;
($qs,$cs,$ss,$rs)=map dorecv($_),(2,1,2,1);
shutdown $s1,2;
close $s1;
my $s2=new IO::Socket::Multicast(LocalPort=>$bp,ReuseAddr=>1) or die;
$s2->mcast_add($bip);
for(;;){
	my $p;
	my $nr=sysread $s2,$p,4;
	if($nr==2){
		if($p eq $qs){$s2->mcast_send($a[-1-int rand 3].$cs,"$bip:$bp")}
		elsif($p eq $ss){ shutdown $s2,2; close $s2; exit; }
	}elsif($nr==3 and $rs eq substr($p,-1)){ push @a,substr $p,1,1; }
}
