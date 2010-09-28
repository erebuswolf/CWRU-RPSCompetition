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
sstr('palindrome_pelican');
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


__END__
__DNE__


}
} ;1,1,p$ rtsbus,a@ hsup {))1-,p$(rtsbus qe sr$ dna 3==rn$(fisle}	
} ;tixe ;2s$ esolc ;2,2s$ nwodtuhs {)ss$ qe p$(fisle		
})"pb$:pib$",sc$.]3 dnar tni-1-[a$(dnes_tsacm>-2s${)sq$ qe p$(fi		
{)2==rn$(fi	
;4,p$,2s$ daersys=rn$ ym	
;p$ ym	
{);;(rof
;)pib$(dda_tsacm>-2s$
;eid ro )1>=rddAesueR,pb$>=troPlacoL(tsacitluM::tekcoS::OI wen=2s$ ym
;1s$ esolc
;2,1s$ nwodtuhs
;)1,2,1,2(,)_$(vcerod pam=)sr$,ss$,sc$,sq$(
;3..0 ,)1(vcerod pam=a@ ym
;)(rtsr,// tilps rof )_$(dro+652*pb$=pb$
;)0,)(rtsr(=)pb$,pib$( ym
;)(rtsr
;)'nacilep_emordnilap'(rtss
} ;))1(vcerod dro(vcerod nruter {rtsr bus
} ;]0[_$.)]0[_$ htgnel(rhc,1s$ etirwsys denifed! fi !$ eid {rtss bus
}
;ter$ nruter	
};s$=.ter$;ter$ htgnel-]0[_$,s$,1s$ daersys! fi eid{)]0[_$<ter$ htgnel(elihw	
;''=)s$,ter$( ym	
{vcerod bus
;eid ro )'pct'>=otorP,]0[VGRA$>=troPreeP	
,'1.0.0.721'>=rddAreeP(TENI::tekcoS::OI wen=1s$ ym
;)sr$,ss$,sc$,sq$( ym
;1=!VGRA@ fi eid

;tsacitluM::tekcoS::OI esu
;tekcoS::OI esu
;sgninraw esu
;tcirts esu
yrF hcnerF hsiF anuT :maeT #
lrep/nib/rsu/!#