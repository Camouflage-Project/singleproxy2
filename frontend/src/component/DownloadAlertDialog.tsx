import React from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import {Typography} from "@mui/material";
import {Platform} from "../enum/Platform";

export default function DownloadAlertDialog(props: DownloadAlertDialogProps) {
    const titleAndContent = getTitleAndContent(props.platform)
    const baseUrl = `${window.location.protocol}//${window.location.hostname}`
    const queryParams = `key=${props.nodeKey}&platform=${props.platform}`
    const unixInstallCommand = `curl -s "${baseUrl}:8080/install?${queryParams}" | sudo bash`
    const downloadLatestUrl = `${baseUrl}:8080/download-latest?${queryParams}`

    return (
        <div>
            <Dialog
                open={props.open}
                onClose={props.handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">{titleAndContent.title}</DialogTitle>
                <DialogContent>
                    <DialogContentText sx={{color: 'black'}} id="alert-dialog-description">
                        {titleAndContent.content}
                    </DialogContentText>
                    {
                        titleAndContent.isUnix
                            ?
                            <DialogContentText id="alert-dialog-description" sx={{mt: 3}}>
                                <Typography component={'span'} color="black">
                                    {unixInstallCommand}
                                </Typography>
                            </DialogContentText>
                            :
                            <DialogActions sx={{justifyContent: 'center', mt: 1}}>
                                <Button href={downloadLatestUrl} variant="outlined"
                                        onClick={props.handleClose}
                                        color="primary" autoFocus>
                                    Download
                                </Button>
                            </DialogActions>
                    }
                </DialogContent>
            </Dialog>
        </div>
    );
}

const getTitleAndContent = (platform: Platform) => {
    const unixInstallContent = "To become a Camouflage residential proxy, copy and paste the below code into a terminal window and press enter:"
    const windowsInstallContent = "To become a Camouflage residential proxy, download and then right click on the downloaded file and run as administrator."

    switch (platform) {
        case Platform.LINUX.valueOf():
            return {
                title: "Linux installation",
                content: unixInstallContent,
                isUnix: true
            }
        case Platform.MAC_OS_APPLE_SILICON.valueOf():
            return {
                title: "MacOS Apple Silicon installation",
                content: unixInstallContent,
                isUnix: true
            }
        case Platform.MAC_OS_INTEL.valueOf():
            return {
                title: "MacOS Intel installation",
                content: unixInstallContent,
                isUnix: true
            }
        case Platform.WINDOWS.valueOf():
            return {
                title: "Windows installation",
                content: windowsInstallContent,
                isUnix: false
            }
        default:
            return {
                title: "Unknown platform",
                content: unixInstallContent,
                isUnix: true
            }
    }
}

interface DownloadAlertDialogProps {
    platform: Platform,
    open: boolean,
    handleClose: () => void
    nodeKey: string
}
