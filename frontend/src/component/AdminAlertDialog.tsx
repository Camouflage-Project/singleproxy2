import React, {useEffect} from 'react';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import axios from 'axios';

export default function AdminAlertDialog(props: AdminDialogProps) {
    const [proxies, setProxies] = React.useState<Array<Proxy>>([])
    const baseUrl = `${window.location.protocol}//${window.location.hostname}:8080`

    const fetchProxies = () => {
        axios.get(`${baseUrl}/proxies`, {
            headers: {
                "key": props.nodeKey
            }
        })
            .then(res => setProxies((res.data as Response).proxies))
    }

    useEffect(fetchProxies, [props.open])

    return (
        <div>
            <Dialog
                open={props.open}
                onClose={props.handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
                fullWidth
                maxWidth="md"
            >
                <DialogTitle id="alert-dialog-title">Your proxies</DialogTitle>
                <DialogContent>
                    {proxies.length === 0 ? "Create your first residential proxy and then find it here!" : getTable(proxies)}
                </DialogContent>
            </Dialog>
        </div>
    );
}

const getTable = (proxies: Array<Proxy>) =>
    <TableContainer component={Paper}>
        <Table sx={{minWidth: 650}} size="small" aria-label="a dense table">
            <TableHead>
                <TableRow>
                    <TableCell align="center">Platform</TableCell>
                    <TableCell align="center">Registered</TableCell>
                    <TableCell align="center">IP Address</TableCell>
                    <TableCell align="center">Last Heartbeat</TableCell>
                    <TableCell align="center">Created</TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {proxies.map((proxy, i) => (
                    <TableRow
                        key={i}
                        sx={{'&:last-child td, &:last-child th': {border: 0}}}
                    >
                        <TableCell align="center">{proxy.platform}</TableCell>
                        <TableCell align="center">{proxy.registered.toString()}</TableCell>
                        <TableCell align="center">{proxy.ipAddress}</TableCell>
                        <TableCell align="center">{formatLocalDateTime(proxy.lastHeartbeat)}</TableCell>
                        <TableCell align="center">{formatLocalDateTime(proxy.created)}</TableCell>
                    </TableRow>
                ))}
            </TableBody>
        </Table>
    </TableContainer>

const formatLocalDateTime = (ldt: LocalDateTime) =>
    `${ldt.year}-${ldt.month}-${ldt.day}-T${ldt.hour}:${ldt.minute}:${ldt.second}`

interface AdminDialogProps {
    open: boolean,
    handleClose: () => void
    nodeKey: string
}

interface Response {
    proxies: Array<Proxy>
}

interface Proxy {
    platform: String,
    registered: Boolean,
    ipAddress: String,
    lastHeartbeat: LocalDateTime,
    created: LocalDateTime
}

interface LocalDateTime {
    year: BigInt,
    month: BigInt,
    day: BigInt,
    hour: BigInt,
    minute: BigInt,
    second: BigInt,
    nanosecond: BigInt
}
