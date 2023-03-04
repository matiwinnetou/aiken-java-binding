package com.bloxbean.cardano.aiken.tx.evaluator;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.SimpleValue;
import com.bloxbean.cardano.aiken.jna.CardanoJNAUtil;
import com.bloxbean.cardano.client.common.cbor.CborSerializationUtil;
import com.bloxbean.cardano.client.exception.CborRuntimeException;
import com.bloxbean.cardano.client.transaction.spec.Redeemer;
import com.bloxbean.cardano.client.util.HexUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EvalPhaseTwoTest {

    @Test
    void eval_phase_two() {
        String tx_hex = "84a600818258202da783ed1161732f1f59319eca1f9da38afde36445ae065b2250a8e6e7362cfe000181825839000d30c6d716fd6c48ab546f0b66fd5faaa3a2f0ccecf0a72ea8c04a30a91cf775fb8e1fdfe882b26014f11d56bd47681270a55fd15e6d064c1a003d090002000d818258202da783ed1161732f1f59319eca1f9da38afde36445ae065b2250a8e6e7362cfe0110825839000d30c6d716fd6c48ab546f0b66fd5faaa3a2f0ccecf0a72ea8c04a30a91cf775fb8e1fdfe882b26014f11d56bd47681270a55fd15e6d064c821b00000002b64c0608a3581c4be3ff6f6ac1303b65adaa8c31b73395f488a6c3370b9694e6df21daa14d4275726e546f6b656e54657374185d581c689903d80b71e0570fea2fdaaa4bf80989785ed2a2cd57da0d9a7d0aa148746f6b656e5f3132197530581cc48f707fea6f08af67a8c06c9bea5b3ec847f5901dc08420cd7f8adea14a4f676d696f73546573741b0000001bf08eb000111a000f4240a20581840000182482000006815907a65907a3010000323322323232323232323232323232323322323232323222232325335323232333573466e1ccc07000d200000201e01d3333573466e1cd55cea80224000466442466002006004646464646464646464646464646666ae68cdc39aab9d500c480008cccccccccccc88888888888848cccccccccccc00403403002c02802402001c01801401000c008cd405c060d5d0a80619a80b80c1aba1500b33501701935742a014666aa036eb94068d5d0a804999aa80dbae501a35742a01066a02e0446ae85401cccd5406c08dd69aba150063232323333573466e1cd55cea801240004664424660020060046464646666ae68cdc39aab9d5002480008cc8848cc00400c008cd40b5d69aba15002302e357426ae8940088c98c80c0cd5ce01881801709aab9e5001137540026ae854008c8c8c8cccd5cd19b8735573aa004900011991091980080180119a816bad35742a004605c6ae84d5d1280111931901819ab9c03103002e135573ca00226ea8004d5d09aba2500223263202c33573805a05805426aae7940044dd50009aba1500533501775c6ae854010ccd5406c07c8004d5d0a801999aa80dbae200135742a00460426ae84d5d1280111931901419ab9c029028026135744a00226ae8940044d5d1280089aba25001135744a00226ae8940044d5d1280089aba25001135744a00226ae8940044d55cf280089baa00135742a00860226ae84d5d1280211931900d19ab9c01b01a018375a00a6eb4014405c4c98c805ccd5ce24810350543500017135573ca00226ea800448c88c008dd6000990009aa80b911999aab9f0012500a233500930043574200460066ae880080508c8c8cccd5cd19b8735573aa004900011991091980080180118061aba150023005357426ae8940088c98c8050cd5ce00a80a00909aab9e5001137540024646464646666ae68cdc39aab9d5004480008cccc888848cccc00401401000c008c8c8c8cccd5cd19b8735573aa0049000119910919800801801180a9aba1500233500f014357426ae8940088c98c8064cd5ce00d00c80b89aab9e5001137540026ae854010ccd54021d728039aba150033232323333573466e1d4005200423212223002004357426aae79400c8cccd5cd19b875002480088c84888c004010dd71aba135573ca00846666ae68cdc3a801a400042444006464c6403666ae7007006c06406005c4d55cea80089baa00135742a00466a016eb8d5d09aba2500223263201533573802c02a02626ae8940044d5d1280089aab9e500113754002266aa002eb9d6889119118011bab00132001355014223233335573e0044a010466a00e66442466002006004600c6aae754008c014d55cf280118021aba200301213574200222440042442446600200800624464646666ae68cdc3a800a40004642446004006600a6ae84d55cf280191999ab9a3370ea0049001109100091931900819ab9c01101000e00d135573aa00226ea80048c8c8cccd5cd19b875001480188c848888c010014c01cd5d09aab9e500323333573466e1d400920042321222230020053009357426aae7940108cccd5cd19b875003480088c848888c004014c01cd5d09aab9e500523333573466e1d40112000232122223003005375c6ae84d55cf280311931900819ab9c01101000e00d00c00b135573aa00226ea80048c8c8cccd5cd19b8735573aa004900011991091980080180118029aba15002375a6ae84d5d1280111931900619ab9c00d00c00a135573ca00226ea80048c8cccd5cd19b8735573aa002900011bae357426aae7940088c98c8028cd5ce00580500409baa001232323232323333573466e1d4005200c21222222200323333573466e1d4009200a21222222200423333573466e1d400d2008233221222222233001009008375c6ae854014dd69aba135744a00a46666ae68cdc3a8022400c4664424444444660040120106eb8d5d0a8039bae357426ae89401c8cccd5cd19b875005480108cc8848888888cc018024020c030d5d0a8049bae357426ae8940248cccd5cd19b875006480088c848888888c01c020c034d5d09aab9e500b23333573466e1d401d2000232122222223005008300e357426aae7940308c98c804ccd5ce00a00980880800780700680600589aab9d5004135573ca00626aae7940084d55cf280089baa0012323232323333573466e1d400520022333222122333001005004003375a6ae854010dd69aba15003375a6ae84d5d1280191999ab9a3370ea0049000119091180100198041aba135573ca00c464c6401866ae700340300280244d55cea80189aba25001135573ca00226ea80048c8c8cccd5cd19b875001480088c8488c00400cdd71aba135573ca00646666ae68cdc3a8012400046424460040066eb8d5d09aab9e500423263200933573801401200e00c26aae7540044dd500089119191999ab9a3370ea00290021091100091999ab9a3370ea00490011190911180180218031aba135573ca00846666ae68cdc3a801a400042444004464c6401466ae7002c02802001c0184d55cea80089baa0012323333573466e1d40052002200923333573466e1d40092000200923263200633573800e00c00800626aae74dd5000a4c240029210350543100320013550032225335333573466e1c0092000005004100113300333702004900119b80002001122002122001112323001001223300330020020011f5f6";
        String inputs = "818258202da783ed1161732f1f59319eca1f9da38afde36445ae065b2250a8e6e7362cfe00";
        String outputs = "81a300581d70b010c0888e93aa488d941ba4839136fceb9b9a9ec310a573299286d7011a003d0900028201d8184108";
        String cost_mdls = "a10198af1a0003236119032c01011903e819023b00011903e8195e7104011903e818201a0001ca761928eb041959d818641959d818641959d818641959d818641959d818641959d81864186418641959d81864194c5118201a0002acfa182019b551041a000363151901ff00011a00015c3518201a000797751936f404021a0002ff941a0006ea7818dc0001011903e8196ff604021a0003bd081a00034ec5183e011a00102e0f19312a011a00032e801901a5011a0002da781903e819cf06011a00013a34182019a8f118201903e818201a00013aac0119e143041903e80a1a00030219189c011a00030219189c011a0003207c1901d9011a000330001901ff0119ccf3182019fd40182019ffd5182019581e18201940b318201a00012adf18201a0002ff941a0006ea7818dc0001011a00010f92192da7000119eabb18201a0002ff941a0006ea7818dc0001011a0002ff941a0006ea7818dc0001011a0011b22c1a0005fdde00021a000c504e197712041a001d6af61a0001425b041a00040c660004001a00014fab18201a0003236119032c010119a0de18201a00033d7618201979f41820197fb8182019a95d1820197df718201995aa18201b00000004a817c8001b00000004a817c8001a009063b91903fd0a1b00000004a817c800001b00000004a817c800";

        SlotConfig.SlotConfigByReference slotConfig = new SlotConfig.SlotConfigByReference();
        slotConfig.zero_time = 1660003200000L;
        slotConfig.zero_slot = 0;
        slotConfig.slot_length = 1000;

        String redeemers = CardanoJNAUtil.eval_phase_two_raw(tx_hex, inputs, outputs, cost_mdls, slotConfig );
        System.out.println(deserializeRedeemerArray(redeemers));

    }

    @Test
    void eval_phase_two_failed() {
        String tx_hex = "84a6008582582068368129e5057b6a314da2216c95087699ef6551fc7d7dcb316277790085f5250082582068368129e5057b6a314da2216c95087699ef6551fc7d7dcb316277790085f5250182582068368129e5057b6a314da2216c95087699ef6551fc7d7dcb316277790085f5250282582068368129e5057b6a314da2216c95087699ef6551fc7d7dcb316277790085f5250382582068368129e5057b6a314da2216c95087699ef6551fc7d7dcb316277790085f525040182a300581d7027bb75b503c762c185b829d924291b24168690e132ff952a1e5b40aa011a00117e5c028201d818583cd8799fa2d8799f1a0ddaf2d51a0011afdeffd8799f02001a006025c2ffd8799f1a0ddaf2d51a00158f74ffd8799f1a00486079001a006025c2ff00ff82583900bee45311d846fc0a8b6bc6c1d06cc212f577efa60086a9c26783c6eb1708ad5cfcd595d49f19695c86d335a339dc8d4130170e38a2cb0c961a004e861c02000d82825820214135a716a3ace3e2e913936af4acf35a78fb45134d2f3c58fcebbb3aab9384018258203974e34e3cde1dc35f06f7bd6c887c927af5277bc270c74fbab06410cf98cefd051082583900bee45311d846fc0a8b6bc6c1d06cc212f577efa60086a9c26783c6eb1708ad5cfcd595d49f19695c86d335a339dc8d4130170e38a2cb0c961b00000004a727c396111a000f4240a20585840000d8799f58383631616564373365663933336163626365303164663764633932633865333264616335663662363032616634616365363161303366613561ff820000840001d8799f58386136366464613232643465323236303737396362393131303330616365623037663938616237353533316330643634356162613361653265ff820000840002d8799f58383038313164323261323933663730653333313265316333623562306332333833383361373030313937363039313933393339396634363532ff820000840003d8799f58383837633732363365386335643562663236326635383736336136383932343433613136633466636232373430356466376330343139343066ff820000840004d8799f58383231343065663462623264643031303963306164616431613262343034386230336165616437636565643233646437313338396534336431ff8200000681590de0590ddd01000032323232323232323232322225333006323232323232323232323232323232323232323232533301b3370e0029000099191919192999810299981018068020998051bac33019301a33019301a01b48001200400114a0266600e02c02a02e2940cdd2a40006604a6e98004cc094dd42400097ae0323232323232323232330020014bd6f7b63000498008009112999816001080089919191919199980e199804004002003199991111999806802001801000a5eb7bdb180cdd2a4000660646ea0008cc0c8dd4000a5eb80cdd2a4000660646ea0dd6998129813001a4004660646ea0dd6998129813001a4008660646ea0dd6998129813001a400c97ae001a01b01a375a66048604a00890031bad33023302400348010c8c8c8c8c8cdd2a400066068008660686ea000ccc0d0dd40011981a1ba80014bd701919806800a40006601a00890001919806000a40006601800690011919805800a400066016004900219ba548000cc0c0dd41bad33023302400148010cc0c0dd41bad330233024001480192f5c00046060006605c004600200244444a66605800826605a66ec000c0092f5bded8c0264646464a66605666ebccc01401c004cdd2a400097ae01330313376000e00c0102a66605666ebc01c0044cc0c4cdd800380300189981899bb000100233333009009003007006005302d003302d0023030005302e004223253330243370e00290010801099190009bad302c001301e003302637540044464664464a66604c66e1c005200213374a900125eb804c8c8cdd2a40006605c6ea0c0100052f5c06eb4c0b8004c08000cc0a0dd500100091bad3301d301e00448008cc00d2f5c301000081010100810102001299981199b87375a66038603a0069004000899b8700200114a06002002444a66604c004266e9520024bd700991929998119801801099ba548000cc0a4dd400125eb804ccc01401400400cc0a800cdd6981400119191919801000919191981219299981219b87001480004c8c8c8c8c8c8c8c8c8c94ccc0ccc0d800852615330304901364c6973742f5475706c652f436f6e73747220636f6e7461696e73206d6f7265206974656d73207468616e2069742065787065637465640016375a606800260680046eb4c0c8004c0c8008dd6981800098180011bad302e001302e002375c6058002603c0042a6604c92012b436f6e73747220696e64657820646964206e6f74206d6174636820616e7920747970652076617269616e740016302637540020026052002603664a66604466e1d20043025375400220022a660489212a4173736572746564206f6e20696e636f727265637420636f6e7374727563746f722076617269616e742e00163301a301b3301a301b0014800920040153001001222533302500214bd7009919198141801801199802802800801981480198138011bae302300130150171533301b3370e002900109919191919191929998112999811180780309980600b8008a50133300901801701914a064646644646660080064466e9520003302e374c66660306eaccc084c08800920003756660426044002900000b80b198171ba80044bd7000099ba548000cc0acdd325eb7bdb180cc0acdd4000a5eb8000c010c00400488894ccc0a800c40044c8c8cccc018018004010cc01000800cc0b8010c0b000cc8c8cc88cc00c0088c8c8cc0a0c94ccc0a0cdc3800a4000264646464a666062606800426605a66054006464646606064a66606066e1c00520001323232325333039303c002149854cc0d9241364c6973742f5475706c652f436f6e73747220636f6e7461696e73206d6f7265206974656d73207468616e2069742065787065637465640016375a607400260740046eb4c0e0004c0a800c54cc0c92412b436f6e73747220696e64657820646964206e6f74206d6174636820616e7920747970652076617269616e740016303237540046606064a66606066e1c00520001323232323232533303b303e002149854cc0e1241364c6973742f5475706c652f436f6e73747220636f6e7461696e73206d6f7265206974656d73207468616e2069742065787065637465640016375a607800260780046eb4c0e8004c0e8008dd6981c00098150010a9981924812b436f6e73747220696e64657820646964206e6f74206d6174636820616e7920747970652076617269616e740016303237540029318190011818000a4c2a6605c921364c6973742f5475706c652f436f6e73747220636f6e7461696e73206d6f7265206974656d73207468616e2069742065787065637465640016375a606400260640046eacc0c0004c08800854cc0a92412b436f6e73747220696e64657820646964206e6f74206d6174636820616e7920747970652076617269616e740016302a3754002a66604e66e1cdd6998101810800a4004008266e9520003302d0014bd70099ba5480092f5c0605a002603e64a66604c66e1d20043029375400220022a6605092012a4173736572746564206f6e20696e636f727265637420636f6e7374727563746f722076617269616e742e00163301e301f3301e301f0014800920040180043001001222533302800214bd70099191919299981399b87001480084ccc01c01c00c0144c8c8cc0bc004ccc02402401401cc0bc004c084008c0a4dd500098018011816001981500119b8000148008dd6981280098128011bae3023001301501714a0603a6ea8058888c8cc02000c8c8c8cdc78008021bae3026001301832533301f3370e900118111baa0011001153302149012a4173736572746564206f6e20696e636f727265637420636f6e7374727563746f722076617269616e742e0016330173018330173018001480012000332232323232001375c6050002603464a66604266e1d20023024375400220022a6604692012a4173736572746564206f6e20696e636f727265637420636f6e7374727563746f722076617269616e742e001633019301a33019301a33019301a00148009200048000c098004c060c94ccc07ccdc3a400060446ea8004400454cc0852412a4173736572746564206f6e20696e636f727265637420636f6e7374727563746f722076617269616e742e00163300600223375e66030603200290000010018009800800911299980f801099ba5480092f5c026464a6660386006004266e952000330220024bd700999802802800801981180198108011119801801119191980e19299980e19b87001480004c8c8c8c94ccc094c0a00084cc084cc07800c8c8c8cc090c94ccc090cdc3800a4000264646464a66605a60600042930a998152481364c6973742f5475706c652f436f6e73747220636f6e7461696e73206d6f7265206974656d73207468616e2069742065787065637465640016375a605c002605c0046eb4c0b0004c07800c54cc0992412b436f6e73747220696e64657820646964206e6f74206d6174636820616e7920747970652076617269616e740016302637540046604864a66604866e1c00520001323232323232533302f3032002149854cc0b1241364c6973742f5475706c652f436f6e73747220636f6e7461696e73206d6f7265206974656d73207468616e2069742065787065637465640016375a606000260600046eb4c0b8004c0b8008dd69816000980f0010a9981324812b436f6e73747220696e64657820646964206e6f74206d6174636820616e7920747970652076617269616e740016302637540029318130011812000a4c2a66044921364c6973742f5475706c652f436f6e73747220636f6e7461696e73206d6f7265206974656d73207468616e2069742065787065637465640016375a604c002604c0046eacc090004c05800854cc0792412b436f6e73747220696e64657820646964206e6f74206d6174636820616e7920747970652076617269616e740016301e375400266ebc010004c084004c04cc94ccc068cdc3a4008603a6ea8004400454cc0712412a4173736572746564206f6e20696e636f727265637420636f6e7374727563746f722076617269616e742e001633012301300148010c0040048894ccc07000852809919299980c98018010a5113330050050010033020003301e00224a2446644a66602c66e400080044cdd2a400097ae0153330163371e004002266e9520024bd70099ba5480112f5c06ecc008dd980091119ba548000cc068cdd2a4000660346ea0cdc01bad3300d300e00248000dd6998069807000a4000660346ea0cdc01bad3300d300e00248008dd6998069807000a4004660346ea0cdc01bad3300d300e00248010dd6998069807000a400897ae04bd7011111999802802001801000980080091111299980c00208018991919191999980480480199999805003800801003002803002980c801980c801180e002980d0021800800911111299980b80289980c19bb00040034bd6f7b630099191919299980b19baf330050080013374a900025eb804cc070cdd80040038048a99980b19baf0080011323253330183370e0029000099191981019bb000c001007302000130120021005301a375400266600c01000e00426603866ec0004008cccccc02802800c02001c018014c06000cc060008c06c018c064014dd61980218029980218028032400090021bac330033004330033004005480012000301000130023253330093370e900118061baa0011001153300b49012a4173736572746564206f6e20696e636f727265637420636f6e7374727563746f722076617269616e742e00163300130020034800888c8ccc0040052000003222333300c3370e008004026466600800866e0000d200230150010012300b37540022930b180080091129998048010a4c26600a600260160046660060066018004002ae695cdab9c5573aaae7955cfaba05742ae881f5f6";
        String inputs = "8582582068368129e5057b6a314da2216c95087699ef6551fc7d7dcb316277790085f5250082582068368129e5057b6a314da2216c95087699ef6551fc7d7dcb316277790085f5250182582068368129e5057b6a314da2216c95087699ef6551fc7d7dcb316277790085f5250282582068368129e5057b6a314da2216c95087699ef6551fc7d7dcb316277790085f5250382582068368129e5057b6a314da2216c95087699ef6551fc7d7dcb316277790085f52504";
        String outputs = "85a300581d7027bb75b503c762c185b829d924291b24168690e132ff952a1e5b40aa011a00133418028201d8185856d8799f5840633263363663326165613536343761653538306230663965636238333235343163326334633964376130653231633037633364616239373436356530383634331a0017c5491a0ddaf2d51a0011afde00ffa300581d7027bb75b503c762c185b829d924291b24168690e132ff952a1e5b40aa011a00133418028201d8185856d8799f5840613661353431653461373064323739373839646262633639316636653065353932383364626261303762373133393866373837366332353363656164346261361a004860791a0ddaf2d51a0011afde00ffa300581d7027bb75b503c762c185b829d924291b24168690e132ff952a1e5b40aa011a00133418028201d8185856d8799f5840613661353431653461373064323739373839646262633639316636653065353932383364626261303762373133393866373837366332353363656164346261361a004860791a0ddaf2d51a00158f7400ffa300581d7027bb75b503c762c185b829d924291b24168690e132ff952a1e5b40aa011a00133418028201d8185856d8799f5840633263363663326165613536343761653538306230663965636238333235343163326334633964376130653231633037633364616239373436356530383634331a0017c5491a0ddaf2d51a00158f7400ffa300581d7027bb75b503c762c185b829d924291b24168690e132ff952a1e5b40aa011a00133418028201d8185856d8799f5840613661353431653461373064323739373839646262633639316636653065353932383364626261303762373133393866373837366332353363656164346261361a004860791a0ddaf2d51a00158f7402ff";
        String cost_mdls = "a10198af1a0003236119032c01011903e819023b00011903e8195e7104011903e818201a0001ca761928eb041959d818641959d818641959d818641959d818641959d818641959d81864186418641959d81864194c5118201a0002acfa182019b551041a000363151901ff00011a00015c3518201a000797751936f404021a0002ff941a0006ea7818dc0001011903e8196ff604021a0003bd081a00034ec5183e011a00102e0f19312a011a00032e801901a5011a0002da781903e819cf06011a00013a34182019a8f118201903e818201a00013aac0119e143041903e80a1a00030219189c011a00030219189c011a0003207c1901d9011a000330001901ff0119ccf3182019fd40182019ffd5182019581e18201940b318201a00012adf18201a0002ff941a0006ea7818dc0001011a00010f92192da7000119eabb18201a0002ff941a0006ea7818dc0001011a0002ff941a0006ea7818dc0001011a0011b22c1a0005fdde00021a000c504e197712041a001d6af61a0001425b041a00040c660004001a00014fab18201a0003236119032c010119a0de18201a00033d7618201979f41820197fb8182019a95d1820197df718201995aa18201a0223accc0a1a0374f693194a1f0a1a02515e841980b30a";

        SlotConfig.SlotConfigByReference slotConfig = new SlotConfig.SlotConfigByReference();
        slotConfig.zero_time = 1660003200000L;
        slotConfig.zero_slot = 0;
        slotConfig.slot_length = 1000;

        String redeemers = CardanoJNAUtil.eval_phase_two_raw(tx_hex, inputs, outputs, cost_mdls, slotConfig );
        assertThat(redeemers).contains("RedeemerError");
    }

    private List<Redeemer> deserializeRedeemerArray(String response) {
        try {
            byte[] redemeersBytes = HexUtil.decodeHexString(response);
            Array redeemerArray = (Array) CborSerializationUtil.deserialize(redemeersBytes);
            List<Redeemer> redeemerList = new ArrayList<>();
            for (DataItem redeemerDI : redeemerArray.getDataItems()) {
                if (redeemerDI == SimpleValue.BREAK)
                    continue;
                Redeemer redeemer = Redeemer.deserialize((Array) redeemerDI);
                redeemerList.add(redeemer);
            }

            return redeemerList;
        } catch (Exception e) {
            throw new CborRuntimeException(e);
        }
    }
}
