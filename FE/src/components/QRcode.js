import React from "react";
import { QRCodeSVG } from "qrcode.react";

const QRcode = (props) => {
    // console.log(`http://domain-url/meeting/${props.paramsNum}`)
    return (
        <div>
            <QRCodeSVG
                // value={"http://localhost:8080/"}
                // value ={`http://domain-url/meeting/${props.paramsNum}`}
                value ={`${props.paramsNum}`}
                size={200}
                bgColor={"#ffffff"}
                fgColor={"#000000"}
                level={"H"}
                includeMargin={false}
            />
        </div>
    )
}

export default QRcode;