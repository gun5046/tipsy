import React from "react";
import { QRCodeSVG } from "qrcode.react";

const QRcode = () => {
    return (
        <div>
            <QRCodeSVG
                value={"http://localhost:8080/"}
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