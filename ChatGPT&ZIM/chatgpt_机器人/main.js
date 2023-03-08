
var ChatGPT, ConversationId, ParentMessageId;
var API_KEY = ;//这里填写openAI的api key
(async () => {
    const { ChatGPTAPI } = await import('chatgpt');
    ChatGPT = new ChatGPTAPI({ apiKey: API_KEY})
})();
const Zego = require('./zego/Zego.js');

var zim;
function onError(err) {
    console.log("on error", err);
}
function chat(text, cb) {
    console.log("正在向ChatGPT发送提问:",text)
    ChatGPT.sendMessage(text, {
        conversationId: ConversationId,
        parentMessageId: ParentMessageId
    }).then(
        function (res) {
            ConversationId = res.conversationId
            ParentMessageId = res.id
            cb && cb(true, res.text)
            console.log(res)
        }
    ).catch(function (err) {
        cb && cb(false, err);
    });
}
function onRcvZegoMsg(isFromGroup, msg, fromUID) {
    var rcvText = msg.message.toLowerCase();
    if (rcvText.indexOf("@chatgpt") >= 0) {
        rcvText = rcvText.replace(/@chatgpt/g, "");
        if (rcvText.length <= 0) return; 
        // Zego.sendMsg(zim, isFromGroup, " ", fromUID, function (succ, err) { })
        chat(rcvText, function (isSucc, text) {
            if (isSucc)
                Zego.sendMsg(zim, isFromGroup, text, fromUID, function (succ, err) {
                    if (!succ) {
                        console.log("回复即构消息发送失败:", msg, err);
                    }
                })
        })
        // ChatGPT.sendMessage(rcvText).then(
        //     function (res) {
        //         console.log(res)
        //         Zego.sendMsg(zim, isFromGroup, res.test, fromUID, function (succ, err) {
        //             if (!succ) {
        //                 console.log("回复即构消息发送失败:", msg, err);
        //             }
        //         })
        //     }
        // );
        // chatGPT.chat(rcvText, function (ans) {
        //     console.log(ans)
        //     Zego.sendMsg(zim, isFromGroup, ans, fromUID, function (succ, err) {
        //         if (!succ) {
        //             console.log("回复即构消息发送失败:", msg, err);
        //         }
        //     })
        // })
    }
}
function main() {
    let zegoChatGPTUID = "chatgpt"
    zim = Zego.initZego(onError, onRcvZegoMsg, zegoChatGPTUID);

}
main();

// const assert = require('assert');
// const { ChatGPTClient } = require('unofficial-chatgpt-api');

// const gpt = new ChatGPTClient({
//     //以下Token需要替换，具体替换方法请阅读本源码关联的文档
//     sessionToken0: 'eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..XngVs5MANQW-Zjpk.bb1CIZopYTeQah6RO2raPEX47l2INNwGPvdaeee0yoh0mslz7-h-WAn3BMVcGx-oMkVeb5teskwKO2N_wKlWpe0asI_Wp4Vm1RjSMk7HjAqH7e5pgpFBfeW9aXjXtO6kfwhSD-eTFxwVr1k60IbKWdrTe-TZjcgwcshe_gZnaALpbSzAlT-mnmtnSYmjlOuoYLOWz7T5IuASj6tYxVQxEzhKVjJ2AbHHjXLoSK6aMdfaXUL3QlVliqBfniCK95QOI9Pa3CMXC-MI2twuYwObBbiteuQ4U9Jxw44sVigIXmPqul6J9_ASVDuqM8sfI_FJfp7Ue57gTiYW__iQWmsUcOLTTRSUlsqOIZDuTIbiiTTN9cGN3wavyeH0Uo_l6GqLJwZhWyFFX0uArkFuKr0jFOsx5xIV4Z0jNqwVUQNkHlKTPkwYUqtXDauVnaqB-YjN1-Ex5DELo6HW0OtLrI8t5Lq-mKud_Qi_LraK6sfiMCGiljWtgDBB8lmmoMvLTmTyRucZ_PZb14-9IgknJLVxjAmzdzqPTpaJPU3SEjmhwZYRGDjC2uBlcWsH_Q8jl_o2fQozSE9cQ2zBAhdomFuQWItlii4Kygvn_mEoR3sONmv81rRTsHXY_Scu4p4rs9RCbeVvtmHTIeOE-t1ODD1hrU8UOsi4OP9o9gyRQB996yXMsx-d8x_JsaHS9FvZAqMoX_nrqyPsVf5-YTZ6d_a3uhI4jVfYFhKQICfySsmT4Bmctb3nv6IZVUPoJ_9lVDby2oLLbwYsxB2zg_tENVwlEHMuB7vRL7FVNEPaYzjwqFYoANA_3S7vQmNqvRqJ1siilAm55Kz2nsa76UXlhAB0vWZoER1hHH_m9SdrstAGj2oOJZsrFl6rHOkTDxRoq1vQetO1CaYJHj54iUpI1NUU4Ws4RyP_teca6nKs8tDfYgs-EWppAoyxWpYT-14VSfWEp6YoRRdGpRkhNS6J_-NYCO60s2Q1PKHg-uz86-yLiX61RhSVxdgNueOGuXtjugRA3IkMTLIhtkZWTTdYSOf_Z4Rdc7d5NJ0e-6-wN4PuW9okZlNFARligtMzVQ9-eq1chCdWrtqlZ1bkGuvhc8jqvlKB1AsB4qy1YLc1IPbclyZNF-m2gUQaGuOph2QT9fR50yqelClml9x9VqR1JdA1aiRFnlTiJ37mtaxI9ybHejsJq6PePsX0oEnFOEouhjrQmyCzFehclHuHuV-8A5tEFlGNuJPP2cwUYP4oeG0uYDTeFeViA_c51embqqRCNmrHyAnuJLzoxWpa7pMYwtDew3pYPov1-9cepLibZ1qcnY0W3nLY4WpxBGR8ngzAYhpHMr4IhNEDCEE2tfzBr5kE0IAd5VB3Wavs9lvITK3Xhduf-GEXymEc5rfvYt7K4kzbBmjllsVXXcp82s1WdX08bhebucBgdiGkYaBjnv1QW4jdlk-ZtLV3ZogobUmze6rB33_Uww77ovBkk-RUD42a4PiYLNgYAFYt2XvX9jy_RD5lz2AsDxiDf18Vr6mM97cO-U6895D6hzpI2ZBdVlepyKefzmknI59tNcaLE3N-bgVxiO-WGYrQB3LEcddrpnzp4JeBvbc5_Z7bZ615l4OAhra5Kki0UQDA1QkNSsYJS0mBEujFL4K4bhhqVC2yS-9m4TsgVTt4xSRv8eIuJwcNOikQ6rfN3G9GhAbzOsiiJJuNrdOcFFJP3V_AVAP-3f_wJFVmX5K1S-xA9DleamNH3eZ6HOzBp4gIKJE8FdZH1uceeeidzMZLYHhGJO60JW276DC9XnMcSWcPvS299z9lTvV-C44Izi_H8obbJnaxBMcj9HeCQ0Iy4J7ya4IFqZgiQn804vdFCQ6Rnc5Z0vchH8QgCoef-b9kd_P-S18-5b1ERW9DyWFjwCNW2fOGTRAPCI4sJtdrOKSS6IX3aDnQTxDvNbHWOLboSOGZbTtnB6pw5BXeHuvL_vgGYsZUxDI3yVGMVN2lmqjbwacOjokBlDfqj9_GWRUb1wk702N0SEQn8ydIaFWcxo9DK65KOrb8OQa2dMkYz2uAxDP-OkWsQh4po9EvZAWuLtLwju7fLRx0CJYNnHS5Z3SBB-JVZGOTK9j9F9Fq55ioU7GtXdN4THrIm1yISqQv5VN-_nYSH0mx-ZyVhvsACdIJ_E8WNzkxIpQNudjjlxs2T39iUdd-MKqhYO2uqv76lGceg9kODm5kuNQ05puaUil_Y23SjTyKEVXCbGiHVbBke3cTAtPqafFy4ZM9uiJvW4DCsXnXCPngBix2vAz2YyBKtNonmFQRAMnxlTHlGX3N9RqFJCQZFPAhHbdm.gm7yuqR788hGsoEAaTA7Qw',
// });
// let convo = null;

// async function chatGPT(question) {
//     if (convo == null) {
//         convo = await gpt.startConversation();
//     }
//     const m1 = await convo.chat(question);
//     return m1.message.content.parts;

// }
// chatGPT("show me some javascript code").then(function (ans) {

//     console.log(ans)
// });
// function main() {
//     chatGPT(convo, "")
//     // try {
//     //     const convo = await gpt.startConversation();
//     //     const m1 = await convo.chat('show me some javascript code:');
//     //     console.log(m1.message.content.parts);

//     //     const m2 = await convo.chat('what was the first question I asked you?');
//     //     assert(
//     //         m2.message.content.parts.find((msg) =>
//     //             msg.toLowerCase().includes('show me some javascript code'),
//     //         ),
//     //         'failed to maintain conversation',
//     //     );

//     //     // // reset the conversation thread
//     //     convo.reset();

//     //     const m3 = await convo.chat('what was the first question I asked you?');
//     //     assert(
//     //         !m3.message.content.parts.find((msg) =>
//     //             msg.toLowerCase().includes('show me some javascript code'),
//     //         ),
//     //         'failed to reset conversation',
//     //     );
//     // } catch (ex) {
//     //     console.error(ex);
//     // }
// }

// // main();
