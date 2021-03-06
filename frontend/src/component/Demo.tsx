import * as React from 'react';
import {useState} from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import ScienceIcon from '@mui/icons-material/Science';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import DownloadAlertDialog from "./DownloadAlertDialog";
import {Platform} from "../enum/Platform";
import AdminAlertDialog from "./AdminAlertDialog";

function Copyright(props: any) {
    return (
        <Typography variant="body2" color="text.secondary" align="center" {...props}>
            {'Copyright © '}
            Camouflage
            {' '}
            {new Date().getFullYear()}
        </Typography>
    );
}

const theme = createTheme({
    palette: {
        primary: {
            main: '#6b705c'
        },
        secondary: {
            main: '#a5a58d'
        }
    }
});

export default function Demo() {
    const [textFieldError, setTextFieldError] = useState(false)
    const [key, setKey] = useState("")
    const [downloadDialogOpen, setDownloadDialogOpen] = React.useState(false);
    const [adminDialogOpen, setAdminDialogOpen] = React.useState(false);
    const [platform, setPlatform] = React.useState(Platform.LINUX)

    const openDownloadAlertDialog = (e: React.MouseEvent<HTMLAnchorElement> | React.MouseEvent<HTMLButtonElement>) => {
        if (key.length === 0) {
            setTextFieldError(true)
            return
        }

        let platform: Platform = Platform[e.currentTarget.id as keyof typeof Platform]
        setPlatform(platform)
        setDownloadDialogOpen(true)
    }

    const openAdminAlertDialog = () => {
        if (key.length === 0) {
            setTextFieldError(true)
            return
        }

        setAdminDialogOpen(true)
    }

    const closeDownloadDialog = () => setDownloadDialogOpen(false)

    const closeAdminDialog = () => setAdminDialogOpen(false)

    const handleTyping = (e: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>) => {
        setKey(e.target.value)
        if (textFieldError) setTextFieldError(false)
    }

    const platformDetails = [
        {platform: Platform.LINUX, name: "Linux"},
        {platform: Platform.WINDOWS, name: "Windows"},
        {platform: Platform.MAC_OS_INTEL, name: "macOS - Intel"},
        {platform: Platform.MAC_OS_APPLE_SILICON, name: "macOS - Apple Silicon"}
    ]

    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="xs">
                <CssBaseline/>
                <Box
                    sx={{
                        mt: 8,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
                        <ScienceIcon/>
                    </Avatar>
                    <Typography sx={{mt: 2}} variant="h4">
                        Camouflage Demo
                    </Typography>
                    <Box sx={{mt: 1}}>
                        <TextField
                            value={key}
                            onChange={handleTyping}
                            error={textFieldError}
                            margin="normal"
                            required
                            fullWidth
                            name="key"
                            label="Key"
                            id="key"
                        />
                        <Button
                            onClick={openAdminAlertDialog}
                            color="secondary"
                            fullWidth
                            variant="contained"
                            sx={{mt: 5}}
                        >
                            admin
                        </Button>
                        {platformDetails.map((p, i) =>
                            <Button
                                key={i}
                                onClick={openDownloadAlertDialog}
                                color="primary"
                                id={p.platform.toString()}
                                fullWidth
                                variant="contained"
                                sx={{mt: 5}}
                            >
                                {p.name}
                            </Button>)}
                    </Box>
                </Box>
                <DownloadAlertDialog platform={platform} open={downloadDialogOpen} handleClose={closeDownloadDialog}
                                     nodeKey={key}/>
                <AdminAlertDialog open={adminDialogOpen} handleClose={closeAdminDialog}
                nodeKey={key}/>
                <Copyright sx={{mt: 8, mb: 4}}/>
            </Container>
        </ThemeProvider>
    );
}
