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
    const baseUrl = `${window.location.protocol}//${window.location.host}`
    const unixInstallCommand = `curl -s ${baseUrl}/install?id=${props.password} | sudo bash`

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
                            <DialogContentText id="alert-dialog-description" sx={{marginTop: 3}}>
                                <Typography component={'span'} sx={{
                                    color: 'black',
                                    background: '#eaedf',
                                    fontSize: 18,
                                    textAlign: 'center',
                                }}>
                                    {unixInstallCommand}
                                </Typography>
                            </DialogContentText>
                            :
                            <DialogActions sx={{justifyContent: 'center'}}>
                                <Button href={baseUrl + "/alealogic-release"} variant="outlined"
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
    const unixInstallContent = "To install, just copy and paste the below code into a terminal window and press enter. Then, proceed to your dashboard."
    const windowsInstallContent = "Click to download and then right click on the downloaded file and click Run as administrator."

    switch (platform) {
        case Platform.Linux.valueOf():
            return {
                title: "Linux installation",
                content: unixInstallContent,
                isUnix: true
            }
        case Platform.MacOsAppleSilicon.valueOf():
            return {
                title: "MacOS Apple Silicon installation",
                content: unixInstallContent,
                isUnix: true
            }
        case Platform.MacOsIntel.valueOf():
            return {
                title: "MacOS Intel installation",
                content: unixInstallContent,
                isUnix: true
            }
        case Platform.Windows.valueOf():
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
    password: string
}
