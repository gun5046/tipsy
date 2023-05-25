import * as React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import QRcode from './QRcode';
import SportsEsportsIcon from '@mui/icons-material/SportsEsports';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: 'background.paper',
  boxShadow: 24,
  p: 4,
  opacity: 0.7
};

const QrModal = (props) => {
  const [open, setOpen] = React.useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  return (
    <div>
      <SportsEsportsIcon onClick={handleOpen}  color="primary" fontSize="large" style={{position: 'absolute', bottom: 20, right: 90, zIndex: 'tooltip', width:'50px', height: '50px'}}/>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <Typography id="modal-modal-title" variant="h4" component="h2" align="center">
            Mobile Game
          </Typography>
          <Typography id="modal-modal-description" sx={{ mt: 2 }} align="center">
            <QRcode paramsNum={props.paramsNum}/>
            You can enter the mobile game with the QR code.
          </Typography>
        </Box>
      </Modal>
    </div>
  );
}


export default QrModal;