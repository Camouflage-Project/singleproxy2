import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import ScienceIcon from '@mui/icons-material/Science';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import axios from "axios";
import {createTheme, ThemeProvider} from '@mui/material/styles';
import {useState} from "react";
import {LinearProgress} from "@mui/material";

function Copyright(props: any) {
    return (
        <Typography variant="body2" color="text.secondary" align="center" {...props}>
            {'Copyright © '}
            Camouflage
            {' '}
            {new Date().getFullYear()}
            {'.'}
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
    const [password, setPassword] = useState("")
    const [spinnerActive, setSpinnerActive] = useState(false)
    let lastSubmittedPassword: string;

    const handleTyping = (e: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>) => {
        setPassword(e.target.value)
        if (textFieldError) setTextFieldError(false)
    }

    const handleClick = (e: React.MouseEvent<HTMLAnchorElement> | React.MouseEvent<HTMLButtonElement>) => {
        if (password.length === 0) {
            setTextFieldError(true)
            return
        }

        setSpinnerActive(true)

        axios.post(
            "http://localhost:8080/download",
            {
                password: password,
                platform: e.currentTarget.id
            }).then( _ => setSpinnerActive(false))

        lastSubmittedPassword = password
    }

    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="xs">
                <CssBaseline/>
                <Box
                    sx={{
                        marginTop: 8,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
                        <ScienceIcon/>
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        Camouflage Demo
                    </Typography>
                    <Box sx={{mt: 1}}>
                        <TextField
                            value={password}
                            onChange={handleTyping}
                            error={textFieldError}
                            margin="normal"
                            required
                            fullWidth
                            name="password"
                            label="Password"
                            id="password"
                        />
                        <Button
                            onClick={handleClick}
                            id="LINUX"
                            fullWidth
                            variant="contained"
                            sx={{mt: 3, mb: 2}}
                        >
                            Linux
                        </Button>
                        <Button
                            onClick={handleClick}
                            id="WINDOWS"
                            fullWidth
                            variant="contained"
                            sx={{mt: 3, mb: 2}}
                        >
                            Windows
                        </Button>
                        <Button
                            onClick={handleClick}
                            id="MACOSINTEL"
                            fullWidth
                            variant="contained"
                            sx={{mt: 3, mb: 2}}
                        >
                            macOS - Intel
                        </Button>
                        <Button
                            onClick={handleClick}
                            id="MACOSAPPLESILICON"
                            fullWidth
                            variant="contained"
                            sx={{mt: 3, mb: 2}}
                        >
                            macOS - Apple Silicon
                        </Button>
                        {spinnerActive && <LinearProgress color="secondary" sx={{marginTop: 3}} />}
                    </Box>
                </Box>
                <Copyright sx={{mt: 8, mb: 4}}/>
            </Container>
        </ThemeProvider>
    );
}
